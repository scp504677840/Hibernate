import entity.HeroEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * hibernate把对象分为四种状态：
 * 1.持久化状态
 * 2.临时状态
 * 3.游离状态
 * 4.删除状态
 */
public class DoWork {
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

           session.doWork(new Work() {
               @Override
               public void execute(Connection connection) throws SQLException {
                   System.out.println(connection);
               }
           });

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