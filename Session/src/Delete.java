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
public class Delete {
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

            // 游离状态
            // HeroEntity heroEntity = new HeroEntity();
            // heroEntity.setId(BigInteger.valueOf(5L));

            // 持久化状态
            HeroEntity heroEntity = session.get(HeroEntity.class, BigInteger.valueOf(4L));

            // <!-- 配置当执行delete方法时将持久化对象或游离对象的OID置为null ；一般实际开发中很少使用。-->
            // <property name="use_identifier_rollback">true</property>

            // 删除状态
            // 删除方式一【推荐】
            session.delete(heroEntity);

            // 删除方式二，其中第一个参数传入entity名称，如果有的话；在实体类@Entity注解里面定义。
            // String entityName="heroEntity";
            // session.delete(entityName,heroEntity);

            transaction.commit();

        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
            ourSessionFactory.close();
        }
    }
    /*
    Hibernate:
    delete
    from
        hero
    where
        id=?
     */
}