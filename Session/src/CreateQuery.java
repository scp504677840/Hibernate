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
public class CreateQuery {
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

            // 全查，不带指定类型
            // fun1(session, transaction);

            // 全查，带指定类型
            // fun2(session, transaction);

            // 全查，带条件
            //fun3(session,transaction);

            // 全查，带条件，命名参数
            //fun4(session, transaction);

            // 全查，分页
            fun5(session, transaction);

        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
            ourSessionFactory.close();
        }
    }

    /**
     * 全查，分页
     *
     * @param session
     * @param transaction
     */
    private static void fun5(Session session, Transaction transaction) {
        String hql = "from HeroEntity ";
        Query<HeroEntity> query = session.createQuery(hql, HeroEntity.class);
        //页数
        int page = 11;
        //每页条目数
        int pageSize = 10;
        List<HeroEntity> heroEntities = query.setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize)
                .list();
        heroEntities.forEach(System.out::println);
        transaction.commit();
    }

    /**
     * 全查，带条件，命名参数
     *
     * @param session
     * @param transaction
     */
    private static void fun4(Session session, Transaction transaction) {
        String hql = "from HeroEntity where id > :id";
        Query<HeroEntity> query = session.createQuery(hql, HeroEntity.class);
        query.setParameter("id", BigInteger.valueOf(3L));
        List<HeroEntity> heroEntities = query.list();
        heroEntities.forEach(System.out::println);
        transaction.commit();
    }

    /**
     * 全查，带条件
     *
     * @param session
     * @param transaction
     */
    private static void fun3(Session session, Transaction transaction) {
        //SQL语句
        String hql = "from HeroEntity where id > ?";
        //创建Query对象
        Query<HeroEntity> query = session.createQuery(hql, HeroEntity.class);
        //设置参数，可以是方法链
        query.setParameter(0, BigInteger.valueOf(3L));
        //执行查询
        List<HeroEntity> heroEntities = query.list();
        //遍历查询结果
        heroEntities.forEach(System.out::println);
        //提交事务
        transaction.commit();
    }

    /**
     * 全查，带指定类型
     *
     * @param session
     * @param transaction
     */
    private static void fun2(Session session, Transaction transaction) {
        String hql = "from HeroEntity ";
        Query<HeroEntity> query = session.createQuery(hql, HeroEntity.class);
        List<HeroEntity> heroEntities = query.list();
        heroEntities.forEach(System.out::println);
        transaction.commit();
    }

    /**
     * 全查，不带指定类型
     *
     * @param session
     * @param transaction
     */
    private static void fun1(Session session, Transaction transaction) {
        String hql = "from HeroEntity ";
        Query query = session.createQuery(hql);

        List<HeroEntity> heroEntities = query.list();

        heroEntities.forEach(System.out::println);

        transaction.commit();
    }
}