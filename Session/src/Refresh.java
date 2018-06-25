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
public class Refresh {
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

            //首先hibernate先去查询数据库，得到记录
            HeroEntity heroEntity = session.get(HeroEntity.class, BigInteger.valueOf(1L));
            System.out.println(heroEntity);
            //此时执行一次select SQL查询操作；打印的name为tom。

            //注意：
            //DEBUG断电处：下面这行代码打上断点，然后手动去修改数据库中的记录。将name改为jack。
            session.refresh(heroEntity);
            System.out.println(heroEntity);
            //此时又执行了一次select SQL查询操作；打印的name为jack。

            // 注意：
            // 如果你在断点处手动改变了数据库中的值，但refresh方法执行后没有效果，那么就是数据库事务隔离级别的问题；
            // 可以去hibernate配置文件中修改数据库事务隔离级别，注意Oracle和MySQL的区别。
            // 例如：MySQL
            // 1：Read Uncommitted 读未提交
            // 2：Read Committed 读已提交
            // 4：Repeatable Read 可重复读
            // 8：Serializable 序列化
            // <!--  设置hibernate事务隔离级别 -->
            // <property name="connection.isolation">2</property>

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
HeroEntity{id=1, gmtCreate=2018-06-24 18:40:31.0, gmtModified=2018-06-24 18:40:31.0, name='Tom'}
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