import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * hibernate把对象分为四种状态：
 * 1.持久化状态
 * 2.临时状态
 * 3.游离状态
 * 4.删除状态
 */
public class CreateNameQuery {
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