package site.nebulas.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import site.nebulas.beans.Response;
import site.nebulas.service.DailySentenceService;
import site.nebulas.service.JudgeService;

import javax.annotation.Resource;


@Controller
public class JudgeController {
	private Logger logger = LoggerFactory.getLogger(JudgeController.class);
	@Resource
	private JudgeService judgeService;
	
	@RequestMapping("api/judge")
	@ResponseBody
	public Object judge(String content){
		Response response = new Response();
		logger.debug("judge debug");

		if (null == content){
			response.setRet(400);
			response.setMsg("content 不能为空");
			return response;
		}
		return judgeService.judge(content);
	}

}
