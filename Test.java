import processing.core.*;
import java.util.Arrays;
import capstone.Capstone;
import processing.event.MouseEvent;
import java.util.*;
import java.util.stream.Collectors;

public class Test extends PApplet{
    final static int entryPointOfProgram = 0x12A0 - 0x1000; //hello world
    final static int IAT_RVA = 0xE14C + 0x400000;
    final static int IAT_SIZE = 0xFC;
    final static int LOCATION_OF_EXITPROCESS = 0x70d0;
//    final static int entryPointOfProgram = 0x12A0 - 0x1000; //brogue
//    final static int entryPointOfProgram = 0x467C60 - 0x400000 - 0x1000;
//    final static int IAT_RVA = 0x12f214 + 0x400000;
//    final static int IAT_SIZE = 0x19c;
//    final static int LOCATION_OF_EXITPROCESS = 0x67910;
//    final static int entryPointOfProgram = 0x12A0 - 0x1000; //lotto
//    final static int IAT_RVA = 0x000F150 + 0x400000;
//    final static int IAT_SIZE = 0xFC + 0x400000;
//    final static int LOCATION_OF_EXITPROCESS = 0x7808;

    //Not super use of static, I admit. But, there shouldn't ever be more than one of these in Test.
    static List<Instruction_Runner> runners = new ArrayList<>();
    static Capstone cs;
    static byte[] bytes;
    Code_Block_Drawer drawer;
    BackgroundFX bgfx;

    public void settings() {
        size(2000, 2000);
    }

    public void setup() {
        //PFont scifiFont = loadFont("MagmawaveCaps-Bold-48.vlw");
        //textFont(scifiFont);
        bgfx = new BackgroundFX(this);

        drawer = new Code_Block_Drawer(this);

        bytes = FileReader.readFile("C:\\Users\\Ecoste\\IdeaProjects\\i-didn-t-think-of-a-name-yet\\helloWorld32.bin");
//        bytes = FileReader.readFile("C:\\Users\\Ecoste\\IdeaProjects\\i-didn-t-think-of-a-name-yet\\brogue.bin");
//        bytes = FileReader.readFile("C:\\Users\\Ecoste\\IdeaProjects\\i-didn-t-think-of-a-name-yet\\lotto.bin");
        System.out.println("File size: " + bytes.length);
        System.out.println("Byte at entry point: " + String.format("%02x", bytes[entryPointOfProgram]));
        printBytes(Arrays.copyOfRange(bytes, entryPointOfProgram, entryPointOfProgram + 0x15));

        cs = new Capstone(Capstone.CS_ARCH_X86, Capstone.CS_MODE_32);
        cs.setDetail(Capstone.CS_OPT_DETAIL); //Turn on detailed mode.

        makeRunner(entryPointOfProgram);
    }

    public void draw(){
        //background(109, 0, 182);
        background(0);
        bgfx.draw();
        stroke(0);

        if (keyPressed ) {
            for (Instruction_Runner runner : runners.stream().filter((r) -> !r.finished && !r.paused).collect(Collectors.toList())) {
                runner.step();
            }
        }

        drawer.draw(runners);
    }

    public void mouseDragged() {
        drawer.mouseDragged();
    }

    public void mouseWheel(MouseEvent event) {
        drawer.mouseWheel(event);
    }

    public void keyPressed() {
        if (key == '+') {
            drawer.zoom(0.1f);
        }

        if (key == '-') {
            drawer.zoom(-0.1f);
        }
    }

    public static void main(String... args){
        PApplet.main("Test");
    }

    public static void printBytes(byte[] bytes) {
        for (byte b : bytes) {
            System.out.print(String.format("%02x", b));
        }
        System.out.print("\n");
    }

    public static Instruction_Runner makeRunner(int startLocation) {
        Instruction_Runner t = new Instruction_Runner(bytes, startLocation, cs, 0);
        runners.add(t);
        return t;
    }

    public static Instruction_Runner makeRunner(int startLocation, Instruction_Runner from_block, int level) {
        Instruction_Runner t = new Instruction_Runner(bytes, startLocation, cs, from_block, level);
        runners.add(t);
        return t;
    }

    public static Instruction_Runner findRunner(int startLocation) {
        for (Instruction_Runner runner : runners) { //Filter too pita here.
            if (runner.startLocation == startLocation) {
                return runner;
            }
        }

        return null;
    }
}
