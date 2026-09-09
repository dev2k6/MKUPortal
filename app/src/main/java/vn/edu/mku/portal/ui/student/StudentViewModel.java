/**
 * Thái Nguyên (dev2k6)
 * 03333 499 48 - 07777 63 858
 */
package vn.edu.mku.portal.ui.student;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import vn.edu.mku.portal.data.local.LanguageManager;
import vn.edu.mku.portal.data.network.model.MenuItem;
import vn.edu.mku.portal.data.network.model.StudentInfoObj1;
import vn.edu.mku.portal.data.network.model.StudentInfoResponse;
import vn.edu.mku.portal.data.network.model.StudentMessage;
import vn.edu.mku.portal.data.repository.StudentRepository;
import vn.edu.mku.portal.data.repository.StudentRepositoryImpl;

public class StudentViewModel extends ViewModel {

    private final StudentRepository studentRepository;
    private final MutableLiveData<StudentState> state = new MutableLiveData<>(StudentState.loading());

    private StudentInfoObj1 currentInfo = null;
    private List<StudentMessage> currentMessages = null;
    private List<MenuItem> currentMenuList = null;
    private int currentUnreadCount = 0;

    public StudentViewModel() {
        this.studentRepository = new StudentRepositoryImpl();
        loadData();
    }

    public LiveData<StudentState> getState() {
        return state;
    }

    public void loadData() {
        state.setValue(StudentState.loading());
        loadStudentInfo();
        loadMessages();
        loadMenu();
    }

    private void loadStudentInfo() {
        studentRepository.fetchStudentInfo(new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(StudentInfoResponse result) {
                if (result != null) {
                    currentInfo = result.getFirstObj1();
                }
                updateState();
            }

            @Override
            public void onError(String errorMessage) {
                state.setValue(StudentState.error(errorMessage, currentInfo, currentMessages, currentMenuList, currentUnreadCount));
            }
        });
    }

    private void loadMessages() {
        studentRepository.fetchMessages(new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<StudentMessage> result) {
                currentMessages = result;
                int count = 0;
                if (result != null) {
                    for (StudentMessage msg : result) {
                        if (msg.getIsRead() == 0) {
                            count++;
                        }
                    }
                }
                currentUnreadCount = count;
                updateState();
            }

            @Override
            public void onError(String errorMessage) {
                // Silently ignore or handle message fetch errors
            }
        });
    }

    private void loadMenu() {
        String lang = LanguageManager.getInstance().getCurrentLanguage();
        studentRepository.fetchMenu(lang, new StudentRepository.ApiCallback<>() {
            @Override
            public void onSuccess(List<MenuItem> result) {
                currentMenuList = result;
                updateState();
            }

            @Override
            public void onError(String errorMessage) {
                // Ignore
            }
        });
    }

    private void updateState() {
        state.setValue(StudentState.success(currentInfo, currentMessages, currentMenuList, currentUnreadCount));
    }
}