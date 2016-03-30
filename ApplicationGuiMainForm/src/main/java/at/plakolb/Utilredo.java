///*	HTL Leonding	*/
//package SavestateListener;
//
//import at.plakolb.controller.AssemblingController;
//import javafx.scene.control.Tab;
//import javafx.scene.control.TabPane;
//
///**
// *
// * @author User
// */
//public abstract class Utilredo {
//    public static void undo(){
//        TabPane tb_assembly=AssemblingController.getInstance().getTb_AssemblingPane();
//        for (Tab object : tb_assembly.getTabs()) {
//            if (object.getText().contains("*")) {
//                object.setText(object.getText().substring(0, object.getText().length()-3));
//            }
//        }
//    }
//}
