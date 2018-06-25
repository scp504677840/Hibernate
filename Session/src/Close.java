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
public class Close {
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

            HeroEntity hero1 = session.get(HeroEntity.class, BigInteger.valueOf(1L));
            HeroEntity hero2 = session.get(HeroEntity.class, BigInteger.valueOf(2L));

            hero1.setName("hero c 1");

            transaction.commit();
            session.close();

            // session关闭，hero2 变成了游离对象，所以无法更新。
            hero2.setName("hero c 2");

            // 下面的执行结果中有两条查询SQL和一条更新SQL。
            // 我们查看数据库得知hero id 为1的更新了，id 为2的没有更新。



        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
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
Hibernate:
    update
        hero
    set
        gmt_create=?,
        gmt_modified=?,
        name=?
    where
        id=?
     */
}