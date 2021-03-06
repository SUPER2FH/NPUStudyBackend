package com.hhstudygroup.npustudy.controller.admin;

import com.hhstudygroup.npustudy.base.BaseApiController;
import com.hhstudygroup.npustudy.base.RestResponse;
import com.hhstudygroup.npustudy.domain.ExamPaperAnswer;
import com.hhstudygroup.npustudy.domain.Subject;
import com.hhstudygroup.npustudy.domain.User;
import com.hhstudygroup.npustudy.service.ExamPaperAnswerService;
import com.hhstudygroup.npustudy.service.SubjectService;
import com.hhstudygroup.npustudy.service.UserService;
import com.hhstudygroup.npustudy.utility.DateTimeUtil;
import com.hhstudygroup.npustudy.utility.ExamUtil;
import com.hhstudygroup.npustudy.utility.PageInfoHelper;
import com.hhstudygroup.npustudy.viewmodel.admin.paper.ExamPaperAnswerPageRequestVM;
import com.hhstudygroup.npustudy.viewmodel.student.exampaper.ExamPaperAnswerPageResponseVM;
import com.hhstudygroup.npustudy.service.*;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("AdminExamPaperAnswerController")
@RequestMapping(value = "/api/admin/examPaperAnswer")
public class ExamPaperAnswerController extends BaseApiController {

    private final ExamPaperAnswerService examPaperAnswerService;
    private final SubjectService subjectService;
    private final UserService userService;

    @Autowired
    public ExamPaperAnswerController(ExamPaperAnswerService examPaperAnswerService, SubjectService subjectService, UserService userService) {
        this.examPaperAnswerService = examPaperAnswerService;
        this.subjectService = subjectService;
        this.userService = userService;
    }


    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public RestResponse<PageInfo<ExamPaperAnswerPageResponseVM>> pageJudgeList(@RequestBody ExamPaperAnswerPageRequestVM model) {
        PageInfo<ExamPaperAnswer> pageInfo = examPaperAnswerService.adminPage(model);
        PageInfo<ExamPaperAnswerPageResponseVM> page = PageInfoHelper.copyMap(pageInfo, e -> {
            ExamPaperAnswerPageResponseVM vm = modelMapper.map(e, ExamPaperAnswerPageResponseVM.class);
            Subject subject = subjectService.selectById(vm.getSubjectId());
            vm.setDoTime(ExamUtil.secondToVM(e.getDoTime()));
            vm.setSystemScore(ExamUtil.scoreToVM(e.getSystemScore()));
            vm.setUserScore(ExamUtil.scoreToVM(e.getUserScore()));
            vm.setPaperScore(ExamUtil.scoreToVM(e.getPaperScore()));
            vm.setSubjectName(subject.getName());
            vm.setCreateTime(DateTimeUtil.dateFormat(e.getCreateTime()));
            User user = userService.selectById(e.getCreateUser());
            vm.setUserName(user.getUserName());
            return vm;
        });
        return RestResponse.ok(page);
    }


}
