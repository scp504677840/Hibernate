import entity.HeroEntity;
import org.hibernate.*;
import org.hibernate.query.Query;
import org.hibernate.cfg.Configuration;

import javax.persistence.metamodel.EntityType;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

/**
 *  hibernate把对象分为四种状态：
 *  1.持久化状态
 *  2.临时状态
 *  3.游离状态
 *  4.删除状态
 */
public class Main {
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
            session.save(new HeroEntity(null, Timestamp.from(Instant.now()),Timestamp.from(Instant.now()),"Tom"));
            session.save(new HeroEntity(null, Timestamp.from(Instant.now()),Timestamp.from(Instant.now()),"Jack"));

            transaction.commit();

        }catch (Exception e){
            transaction.rollback();
            e.printStackTrace();
        }finally {
            session.close();
            ourSessionFactory.close();
        }
    }
}