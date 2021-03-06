package com.hhstudygroup.npustudy.controller.student;

import com.hhstudygroup.npustudy.base.BaseApiController;
import com.hhstudygroup.npustudy.base.RestResponse;
import com.hhstudygroup.npustudy.domain.ExamPaper;
import com.hhstudygroup.npustudy.service.ExamPaperAnswerService;
import com.hhstudygroup.npustudy.service.ExamPaperService;
import com.hhstudygroup.npustudy.utility.DateTimeUtil;
import com.hhstudygroup.npustudy.utility.PageInfoHelper;
import com.hhstudygroup.npustudy.viewmodel.admin.exam.ExamPaperEditRequestVM;
import com.hhstudygroup.npustudy.viewmodel.student.exam.ExamPaperPageResponseVM;
import com.hhstudygroup.npustudy.viewmodel.student.exam.ExamPaperPageVM;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController("StudentExamPaperController")
@RequestMapping(value = "/api/student/exam/paper")
public class ExamPaperController extends BaseApiController {

    private final ExamPaperService examPaperService;
    private final ExamPaperAnswerService examPaperAnswerService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ExamPaperController(ExamPaperService examPaperService, ExamPaperAnswerService examPaperAnswerService, ApplicationEventPublisher eventPublisher) {
        this.examPaperService = examPaperService;
        this.examPaperAnswerService = examPaperAnswerService;
        this.eventPublisher = eventPublisher;
    }


    @RequestMapping(value = "/select/{id}", method = RequestMethod.POST)
    public RestResponse<ExamPaperEditRequestVM> select(@PathVariable Integer id) {
        ExamPaperEditRequestVM vm = examPaperService.examPaperToVM(id);
        return RestResponse.ok(vm);
    }


    @RequestMapping(value = "/pageList", method = RequestMethod.POST)
    public RestResponse<PageInfo<ExamPaperPageResponseVM>> pageList(@RequestBody @Valid ExamPaperPageVM model) {
        PageInfo<ExamPaper> pageInfo = examPaperService.studentPage(model);
        PageInfo<ExamPaperPageResponseVM> page = PageInfoHelper.copyMap(pageInfo, e -> {
            ExamPaperPageResponseVM vm = modelMapper.map(e, ExamPaperPageResponseVM.class);
            vm.setCreateTime(DateTimeUtil.dateFormat(e.getCreateTime()));
            return vm;
        });
        return RestResponse.ok(page);
    }
}
