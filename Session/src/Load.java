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
public class Load {
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

            // 临时状态
            // get方法获取数据
            //HeroEntity heroEntity = session.get(HeroEntity.class, BigInteger.valueOf(1L));
            // load方法获取数据
            HeroEntity heroEntity = session.load(HeroEntity.class, BigInteger.valueOf(1L));
            System.out.println(heroEntity);

            // get和load有什么区别？
            // 1.执行get方法会立即加载对象；执行load方法若不使用该对象则不会执行查询操作，而返回一个代理对象。
            // 2.get是立即检索；load是延迟检索。
            // 3.若数据表中没有唯一对应记录，get返回null，load抛出异常。
            // 4.在代理对象创建之前若关闭session则会抛出LazyInitializationException异常。

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