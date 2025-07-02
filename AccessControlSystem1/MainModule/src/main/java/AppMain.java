//文件: AppMain.java ///////////////////////////////////////////////////////////////////////////////////////////

import javax.swing.*;

//import com.wan.dao.UserDAO;
import com.ncu.common.dao.UserDAO;
import com.ncu.mainmodule.view.LoginView;

public class AppMain {

    public static void main(String[] args) {
        //
        //
        //
        //
        //
        //
        //
        //
        //
        // new UserDAO().updateUserPassword(1, "666666");
        // 启动登录界面
        SwingUtilities.invokeLater(() -> {
            new LoginView().showLogin();
        });
    }

}