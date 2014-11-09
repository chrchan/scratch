package sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class RangeManagerTest {
    RangeManager rangeMan = new RangeManager();

    @Test
    public void testAdd() {
        rangeMan.add(new Range(12, 16));
        assertEquals(1, rangeMan.toList().size());
        assertRange(0, 12, 16);

        // Add non-overlapping range
        rangeMan.add(new Range(3, 8));
        assertEquals(2, rangeMan.toList().size());
        assertRange(0, 3, 8);
        assertRange(1, 12, 16);

        // For overlap, extend existing range
        rangeMan.add(new Range(5, 10));
        assertEquals(2, rangeMan.toList().size());
        assertRange(0, 3, 10);
        assertRange(1, 12, 16);
    }

    @Test
    public void testRemove() {
        rangeMan.add(new Range(3, 16));

        // Remove from middle
        rangeMan.remove(new Range(13, 14));
        assertEquals(2, rangeMan.toList().size());
        assertRange(0, 3, 12);
        assertRange(1, 15, 16);

        // Add it back
        rangeMan.add(new Range(13, 14));
        assertEquals(1, rangeMan.toList().size());
        assertRange(0, 3, 16);

        // Remove from end
        rangeMan.remove(new Range(10, 20));
        assertEquals(1, rangeMan.toList().size());
        assertRange(0, 3, 9);

        // Remove outer range
        rangeMan.remove(new Range(1, 20));
        assertEquals(0, rangeMan.toList().size());
    }

    @Test
    public void testToList() {
        // Sanity test empty case
        List<Range> list = rangeMan.toList();
        assertEquals(0, list.size());
    }

    private void assertRange(int index, int low, int high) {
        List<Range> list = rangeMan.toList();
        assertTrue(list.size() >= index);
        Range r = list.get(index);
        assertEquals(low, r.getLow());
        assertEquals(high, r.getHigh());
    }

}
