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
public class Clear {
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

            // 执行查询操作，将结果加入一级缓存
            HeroEntity heroEntity1 = session.get(HeroEntity.class, BigInteger.valueOf(1L));
            System.out.println(heroEntity1);

            // 清理缓存
            session.clear();

            // 由于session清理了缓存，于是再执行同样的查询操作也会去查询数据库。
            // hibernate 前后总共两次访问了数据库。
            HeroEntity heroEntity2 = session.get(HeroEntity.class, BigInteger.valueOf(1L));
            System.out.println(heroEntity2);

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
    select
        heroentity0_.id as id1_0_0_,
        heroentity0_.gmt_create as gmt_crea2_0_0_,
        heroentity0_.gmt_modified as gmt_modi3_0_0_,
        heroentity0_.name as name4_0_0_
    from
        hero heroentity0_
    where
        heroentity0_.id=?
HeroEntity{id=1, gmtCreate=2018-06-24 18:40:31.0, gmtModified=2018-06-24 18:40:31.0, name='Cher'}
Hibernate:
    select
        heroentity0_.id as id1_0_0_,
        heroentity0_.gmt_create as gmt_crea2_0_0_,
        heroentity0_.gmt_modified as gmt_modi3_0_0_,
        heroentity0_.name as name4_0_0_
    from
        hero heroentity0_
    where
        heroentity0_.id=?
HeroEntity{id=1, gmtCreate=2018-06-24 18:40:31.0, gmtModified=2018-06-24 18:40:31.0, name='Cher'}
     */
}