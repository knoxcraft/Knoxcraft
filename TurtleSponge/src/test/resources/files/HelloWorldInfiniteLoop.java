import org.knoxcraft.turtle3d.Turtle3D;
import static org.knoxcraft.turtle3d.KCTBlockTypes.*;

public class HelloWorld {
  public static void main(String[] args) {

    Turtle3D t=Turtle3D.createTurtle("bob");
    t.setBlock(RED_WOOL);
    t.forward(10);
    if(true) while (true);
    // Infinite loop!
    
  }
}