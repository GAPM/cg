
import org.junit.Assert;
import org.junit.Test;

import main.GL;

public class GLTest {
    @Test
    public void bothSideCrop() {
        Assert.assertEquals(GL.bothSideCrop("Simon"), "imo");
        Assert.assertEquals(GL.bothSideCrop("as"), "");
        Assert.assertEquals(GL.bothSideCrop("O"), "");
    }
}