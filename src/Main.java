import com.formdev.flatlaf.FlatDarculaLaf;// The new theme
import view.LoginView;                   // Your login screen
import javax.swing.UIManager;            // Required to set the theme

public class Main {
    public static void main(String[] args) {
        // 1. SETUP THEME (Must happen before opening any window)
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception e) {
            System.out.println("Failed to initialize FlatLaf");
            e.printStackTrace();
        }

        // 2. LAUNCH APP
        java.awt.EventQueue.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}