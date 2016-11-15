package org.davidmoten.rx.jdbc.pool;

import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.Tx;
import org.junit.Test;

import io.reactivex.Flowable;

public class DatabaseTest {

    private static Database db() {
        return DatabaseCreator.create(1);
    }

    @Test
    public void testSelectUsingQuestionMark() {
        db() //
                .select("select score from person where name=?") //
                .parameters("FRED", "JOSEPH") //
                .getAs(Integer.class) //
                .test() //
                .assertValues(21, 34) //
                .assertComplete();
    }

    @Test
    public void testSelectUsingName() {
        db() //
                .select("select score from person where name=:name") //
                .parameter("name", "FRED") //
                .parameter("name", "JOSEPH") //
                .getAs(Integer.class) //
                .test() //
                .assertValues(21, 34) //
                .assertComplete();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectUsingNameWithoutSpecifyingNameThrowsImmediately() {
        db() //
                .select("select score from person where name=:name") //
                .parameters("FRED", "JOSEPH");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectParametersSpecifiedWhenNoneExpectedThrowsImmediately() {
        db() //
                .select("select score from person") //
                .parameters("FRED", "JOSEPH");
    }

    @Test
    public void testSelectTransacted() {
        Flowable<Tx<Integer>> o = db() //
                .select("select score from person where name=?") //
                .parameters("FRED", "JOSEPH") //
                .getInTransaction(Integer.class);
        o.test() //
                .assertValueCount(3) //
                .assertComplete();
    }

}