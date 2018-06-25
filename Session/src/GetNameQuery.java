import entity.HeroEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.math.BigInteger;
import java.util.List;

/**
 * hibernate把对象分为四种状态：
 * 1.持久化状态
 * 2.临时状态
 * 3.游离状态
 * 4.删除状态
 */
public class GetNameQuery {
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

            // 命名查询，不带参数
            //fun1(session, transaction);

            // 命名查询，带命名参数
            fun2(session, transaction);

        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
            ourSessionFactory.close();
        }
    }

    /**
     * 命名查询，带命名参数
     *
     * @param session
     * @param transaction
     */
    private static void fun2(Session session, Transaction transaction) {
        Query query = session.getNamedQuery("where");
        List<HeroEntity> heroes = query.setParameter("id", BigInteger.valueOf(3))
                .setFirstResult(0)
                .setMaxResults(3)
                .list();
        heroes.forEach(System.out::println);
        transaction.commit();
    }

    /**
     * 命名查询，不带参数
     *
     * @param session
     * @param transaction
     */
    private static void fun1(Session session, Transaction transaction) {
        Query query = session.getNamedQuery("all");
        List<HeroEntity> list = query.setFirstResult(0).setMaxResults(10).list();
        list.forEach(System.out::println);
        transaction.commit();
    }
}