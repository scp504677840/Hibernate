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
public class Get {
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
        //get
        //get
        //get
        //get
        //get
        //get
        final Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {

            //1.我们知道，当我们执行查询操作的时候，session就会按照要求帮我们去查询数据库。
            // 执行 session.get方法时hibernate会向数据库发送一条查询SQL。那么请问：
            // 下面两段 session.get方法代码执行时，hibernate会向数据库发送几条SQL语句？
            // 答案是1条，因为session有一级缓存。
            HeroEntity heroEntity1 = session.get(HeroEntity.class, BigInteger.valueOf(1L));
            System.out.println(heroEntity1);
            HeroEntity heroEntity2 = session.get(HeroEntity.class, BigInteger.valueOf(1L));
            System.out.println(heroEntity2);

            //提交事务
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