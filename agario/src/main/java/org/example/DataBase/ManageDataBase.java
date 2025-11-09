package org.example.DataBase;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ManageDataBase
{
    private SessionFactory sessionFactory;
    public ManageDataBase()
    {
        try
        {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        }
        catch(Exception e)
        {
            System.out.println("Coudn't create session: " + e);
            System.exit(1);
        }
    }

    private boolean ifPlayerNameIsFree(String playerName)
    {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        boolean check = true;

        try
        {
            transaction = session.beginTransaction();
            String hql = "Select t from Players t where t.playerName =:name";
            Players player = session.createSelectionQuery(hql, Players.class).setParameter("name", playerName).uniqueResult();
            transaction.commit();
            if(player == null) check = false;
        }
        catch (Exception e)
        {
            System.err.println("Could not check whether Player exists" + e);
            System.exit(5);
        }
        finally {
            session.close();
        }
        return check;
    }
    public void addScore(Players dataBase)
    {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        if(ifPlayerNameIsFree(dataBase.getPlayerName()))
        {
            System.out.println("User name with this Nick exits!");
            return;
        }

        try
        {
            transaction = session.beginTransaction();
            session.persist(dataBase);
            transaction.commit();
        }
        catch (Exception e)
        {
            System.err.println("The user could not be added to database: " + e);
        }


    }
}
