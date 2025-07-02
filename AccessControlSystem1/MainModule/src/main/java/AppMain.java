import com.ncu.common.dao.UserDAO;
import com.ncu.common.model.User;
import com.ncu.mainmodule.view.LoginView;

import javax.swing.*;

public class AppMain {
    public static void main(String[] args) {
        // 确保admin用户存在
        UserDAO userDAO = new UserDAO();
        if (userDAO.getUserByUsername("admin") == null) {
            User admin = new User(0, "admin", "123456", "admin", false,
                    "管理员", "13800000000", null, "男", null);
            userDAO.addUser(admin);
        }

        // 启动登录界面
        SwingUtilities.invokeLater(() -> {
            new LoginView().showLogin();
        });
    }
}