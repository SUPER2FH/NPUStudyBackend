package com.hhstudygroup.npustudy.controller.wx.student;

import com.hhstudygroup.npustudy.base.RestResponse;
import com.hhstudygroup.npustudy.controller.wx.BaseWXApiController;
import com.hhstudygroup.npustudy.domain.ExamPaper;
import com.hhstudygroup.npustudy.domain.Subject;
import com.hhstudygroup.npustudy.service.ExamPaperService;
import com.hhstudygroup.npustudy.service.SubjectService;
import com.hhstudygroup.npustudy.utility.DateTimeUtil;
import com.hhstudygroup.npustudy.utility.PageInfoHelper;
import com.hhstudygroup.npustudy.viewmodel.admin.exam.ExamPaperEditRequestVM;
import com.hhstudygroup.npustudy.viewmodel.student.exam.ExamPaperPageResponseVM;
import com.hhstudygroup.npustudy.viewmodel.student.exam.ExamPaperPageVM;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller("WXStudentExamController")
@RequestMapping(value = "/api/wx/student/exampaper")
@ResponseBody
public class ExamPaperController extends BaseWXApiController {

    private final ExamPaperService examPaperService;
    private final SubjectService subjectService;

    @Autowired
    public ExamPaperController(ExamPaperService examPaperService, SubjectService subjectService) {
        this.examPaperService = examPaperService;
        this.subjectService = subjectService;
    }


    @RequestMapping(value = "/select/{id}", method = RequestMethod.POST)
    public RestResponse<ExamPaperEditRequestVM> select(@PathVariable Integer id) {
        ExamPaperEditRequestVM vm = examPaperService.examPaperToVM(id);
        return RestResponse.ok(vm);
    }


    @RequestMapping(value = "/pageList", method = RequestMethod.POST)
    public RestResponse<PageInfo<ExamPaperPageResponseVM>> pageList(@Valid ExamPaperPageVM model) {
        model.setLevelId(getCurrentUser().getUserLevel());
        PageInfo<ExamPaper> pageInfo = examPaperService.studentPage(model);
        PageInfo<ExamPaperPageResponseVM> page = PageInfoHelper.copyMap(pageInfo, e -> {
            ExamPaperPageResponseVM vm = modelMapper.map(e, ExamPaperPageResponseVM.class);
            Subject subject = subjectService.selectById(vm.getSubjectId());
            vm.setSubjectName(subject.getName());
            vm.setCreateTime(DateTimeUtil.dateFormat(e.getCreateTime()));
            return vm;
        });
        return RestResponse.ok(page);
    }
}
