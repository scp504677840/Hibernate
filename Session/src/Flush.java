import entity.HeroEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.math.BigInteger;

/**
 * hibernate把对象分为四种状态：
 * 1.持久化状态
 * 2.临时状态
 * 3.游离状态
 * 4.删除状态
 */
public class Flush {
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

            //1.我们知道，当我们执行查询操作的时候，session就会按照要求帮我们去查询数据库。
            // 执行 session.get方法时hibernate会向数据库发送一条查询SQL。那么请问：
            // 下面两段 session.get方法代码执行时，hibernate会向数据库发送几条SQL语句？
            // 答案是1条，因为session有一级缓存。
            HeroEntity heroEntity = session.get(HeroEntity.class, BigInteger.valueOf(1L));
            System.out.println(heroEntity);
            //下面这段代码被动执行了session的flush方法，因为session感知到了对象的数据发生了改变，
            // 为了与数据库中的数据保持一致，那么就触发了flush方法。
            // transaction.commit()中执行顺序：先调用session的flush方法，再提交事务。
            heroEntity.setName("Cher");

            //显式的调用flush方法
            //session.flush();

            // Q：flush和commit的区别？
            // A：flush会执行一系列SQL，但不会提交事务。
            // commit会在提交事务之前调用flush方法，然后再提交事务。

            // Q：flush方法的特例？
            // A：1.执行HQL或QBC语句时，会先进行flush操作，已得到数据表的最新记录。
            // 2.若记录ID是由底层数据库使用自增的方式生成的，则在调用save方法时，就会立即发送insert语句，
            // 因为在save方法执行后，对象的ID必须是存在的！

            // flush是把session中的对象刷到数据库。
            // refresh是把数据库中的数据刷到session中的对象。

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