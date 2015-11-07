import main.GL;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class GLTest {
    @Test
    public void bothSideCrop() {
        Assert.assertEquals(GL.bothSideCrop("Simon"), "imo");
        Assert.assertEquals(GL.bothSideCrop("as"), "");
        Assert.assertEquals(GL.bothSideCrop("O"), "");
    }

    @Test
    public void findExec() {
        Assert.assertNotEquals(GL.findExec("gcc"), Optional.empty());
    }
}