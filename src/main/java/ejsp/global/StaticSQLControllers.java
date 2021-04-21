package ejsp.global;

import ejsp.SQLController;
import ejsp.exceptions.GlobalControlException;
import java.util.ArrayList;

public class StaticSQLControllers {
    private static ArrayList<SQLController> globalDBList = new ArrayList<>();
    private static ArrayList<String> globalDBListName = new ArrayList<>();

    private static boolean parallelArrayMatch() {
        return globalDBListName.size() == globalDBList.size();
    }

    public static void addController(SQLController initializedSQLController, String controllerName) throws GlobalControlException {
        if (globalDBListName.contains(controllerName)) {
            throw new GlobalControlException("The controller with the name of \"" + controllerName + "\" already exists.");
        }else{
            globalDBList.add(initializedSQLController);
            globalDBListName.add(controllerName);
        }
    }

    public static ArrayList<SQLController> getAllControllers() throws GlobalControlException {
        if (!parallelArrayMatch()){
            throw new GlobalControlException("The controller list and the name list is broken. (Internal Exception)");
        }else{
            return globalDBList;
        }
    }

    public static SQLController getController(String controllerName) throws GlobalControlException {
        if (!globalDBListName.contains(controllerName)) {
            throw new GlobalControlException("The controller with the name of \"" + controllerName + "\" does not exist.");
        }else if (!parallelArrayMatch()){
            throw new GlobalControlException("The controller list and the name list is broken. (Internal Exception)");
        }else{
            return globalDBList.get(globalDBListName.indexOf(controllerName));
        }
    }

    public static void removeController(int indexOf) throws GlobalControlException {
        if (globalDBList.size() <= indexOf) {
            throw new GlobalControlException("Controller index out of bound.");
        }else if (!parallelArrayMatch()) {
            throw new GlobalControlException("The controller list and the name list is broken. (Internal Exception)");
        }else {
            globalDBList.remove(indexOf);
            globalDBListName.remove(indexOf);
        }
    }

    public static void removeController(String controllerName) throws GlobalControlException {
        if (!globalDBListName.contains(controllerName)) {
            throw new GlobalControlException("Controller not found.");
        }else if (!parallelArrayMatch()) {
            throw new GlobalControlException("The controller list and the name list is broken. (Internal Exception)");
        }else{
            globalDBList.remove(globalDBListName.indexOf(controllerName));
            globalDBListName.remove(globalDBListName.indexOf(controllerName));
        }
    }

    public static int getControllerCounts() {
        return globalDBList.size();
    }
}
