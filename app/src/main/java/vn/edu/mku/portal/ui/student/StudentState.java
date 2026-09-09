package vn.edu.mku.portal.ui.student;

import java.util.List;

import vn.edu.mku.portal.data.network.model.MenuItem;
import vn.edu.mku.portal.data.network.model.StudentInfoObj1;
import vn.edu.mku.portal.data.network.model.StudentMessage;
import vn.edu.mku.portal.ui.common.Event;

public class StudentState {

    private final boolean isLoading;
    private final StudentInfoObj1 studentInfo;
    private final List<StudentMessage> messages;
    private final List<MenuItem> menuList;
    private final int unreadMessageCount;
    private final Event<String> errorMessageEvent;

    public StudentState(boolean isLoading,
                        StudentInfoObj1 studentInfo,
                        List<StudentMessage> messages,
                        List<MenuItem> menuList,
                        int unreadMessageCount,
                        Event<String> errorMessageEvent) {
        this.isLoading = isLoading;
        this.studentInfo = studentInfo;
        this.messages = messages;
        this.menuList = menuList;
        this.unreadMessageCount = unreadMessageCount;
        this.errorMessageEvent = errorMessageEvent;
    }

    public static StudentState loading() {
        return new StudentState(true, null, null, null, 0, null);
    }

    public static StudentState success(StudentInfoObj1 info, List<StudentMessage> messages, List<MenuItem> menuList, int unreadCount) {
        return new StudentState(false, info, messages, menuList, unreadCount, null);
    }

    public static StudentState error(String errorMessage, StudentInfoObj1 currentInfo, List<StudentMessage> messages, List<MenuItem> menuList, int unreadCount) {
        return new StudentState(false, currentInfo, messages, menuList, unreadCount, new Event<>(errorMessage));
    }

    public boolean isLoading() {
        return isLoading;
    }

    public StudentInfoObj1 getStudentInfo() {
        return studentInfo;
    }

    public List<StudentMessage> getMessages() {
        return messages;
    }

    public List<MenuItem> getMenuList() {
        return menuList;
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public Event<String> getErrorMessageEvent() {
        return errorMessageEvent;
    }
}