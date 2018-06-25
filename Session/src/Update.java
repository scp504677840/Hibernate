import entity.HeroEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * hibernate把对象分为四种状态：
 * 1.持久化状态
 * 2.临时状态
 * 3.游离状态
 * 4.删除状态
 */
public class Update {
    private static final SessionFactory ourSessionFactory;

    static {
        try {
            //使用加载hibernate.cfg.xml方式配置hibernate
            Configuration configuration = new Configuration().configure();
            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    public static void main(final String[] args) throws Exception {
        final Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {

            // 情况一：
            // 持久化状态
            //HeroEntity hero = session.get(HeroEntity.class, BigInteger.valueOf(2L));
            //  当此对象存在并开启事务时，对象设置其属性，ID除外，那么事务提交的时候就会自动执行update SQL更新对象。
            // 因为session里面的hero对象已经和数据库中的记录不一致了，事务提交前会先执行flush方法，
            // 而flush方法就是保持session中的对象和数据库中记录保持一直。
            //hero.setName("Er2");

            // 注意：虽然上述情况中我们不用显式的调用update方法去更新对象，
            // 但是不意味着我们不可以再显式的调用update方法，如果我们再显式的调用update方法，
            // 会再次执行update SQL语句。这样的话没有必要，也造成资源的浪费。
            // 那么如何避免这种情况呢？
            // 可以在 .hbm.xml 配置文件中将select-before-update属性置为true。
            // 意思就是说在更新前先查询此记录是否已经和session中的记录一致，如果一致则不执行update SQL；不一致则执行update SQL。
            // 但是如果开启此属性，效率会降低。所以一般实际开发中很少使用。
            // <class name="entity.HeroEntity" table="hero" schema="lab" select-before-update="true">
            // 显式的调用update方法，一般来说在开启事务的时候不必显式的调用update方法。
            // session.update(hero);

            // 情况二：
            // 也就是什么时候需要显式的调用update方法呢？
            // 临时状态和游离状态的时候需要显式的调用update方法。
            HeroEntity heroEntity = new HeroEntity(Timestamp.from(Instant.now()), Timestamp.from(Instant.now()), "Yi5");
            heroEntity.setId(BigInteger.valueOf(5L));

            // 持久化状态
            // 更新方式一【推荐】
            session.update(heroEntity);

            // 更新方式二，其中第一个参数传入entity名称，如果有的话；在实体类@Entity注解里面定义。
            // String entityName="heroEntity";
            // session.update(entityName,heroEntity);

            // 小心坑：
            // 第一个坑：不是同一个session，也就说我们上面创建了一个session1，
            // 当把id为1的对象查出来的同时关闭session1，此时id为1的对象变成了游离对象，
            // 然后再创建一个新的session2，再将id为1的对象设置其属性并提交事务或flush时，将不会执行update SQL。
            // 因为保存id为1的对象session1已经关闭了。此时就需要session2显式的调用update方法来更新id为1的对象。
            //transaction.commit();
            //session.close();

            // 第二个坑：
            // session已经关闭，hero变成游离状态，此时我们再去更改hero的id属性为一个数据库中不存在的记录，
            // 然后执行session的update方法，则会抛出异常。
            //heroEntity.setId(BigInteger.valueOf(1L));

            //session = getSession();
            //transaction = session.beginTransaction();

            //session.update(heroEntity);

            transaction.commit();

        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
            ourSessionFactory.close();
        }
    }
}