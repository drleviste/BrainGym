package com.patateam.braingym.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.patateam.braingym.dao.CategoryDAO;
import com.patateam.braingym.dao.QuizDAO;
import com.patateam.braingym.dao.QuizTagDAO;
import com.patateam.braingym.dao.TagDAO;
import com.patateam.braingym.model.Category;
import com.patateam.braingym.model.Quiz;
import com.patateam.braingym.model.Tag;



@Controller
public class QuizController {
	@Autowired private QuizDAO quizDAO;
	@Autowired private CategoryDAO categoryDAO;
	@Autowired private TagDAO tagDAO;
	@Autowired private QuizTagDAO quizTagDAO;
	
	private static final Logger logger = LoggerFactory.getLogger(QuizController.class);
	  /**
	   * This handler method is invoked when
	   * http://localhost:8080/pizzashop is requested.
	   * The method returns view name "index"
	   * which will be resolved into /WEB-INF/index.jsp.
	   *  See src/main/webapp/WEB-INF/servlet-context.xml
	   */
	  @RequestMapping(value = "/quizList", method = RequestMethod.POST)
	  public String list(@RequestParam(required=false) long categoryid, @RequestParam(required=false) String tag, Model model) {
		//long catid = category.getCatid();
		if(categoryid!=0 && !tag.isEmpty()){
			
			List<Quiz> quizzes = quizDAO.findAll(categoryid, tag);
		    model.addAttribute("quizzes", quizzes);
		}
		else if(categoryid!=0){
			List<Quiz> quizzes = quizDAO.findAll(categoryid);
		    model.addAttribute("quizzes", quizzes);
		}
		else if(!tag.isEmpty()){
			List<Quiz> quizzes = quizDAO.findAll(tag);
		    model.addAttribute("quizzes", quizzes);
		}
		else if(categoryid==0){
			List<Quiz> quizzes = quizDAO.findAll();
		    model.addAttribute("quizzes", quizzes);
		}
	    
	    return "quizList";
	  }
	  @RequestMapping(value = "/addQuiz", method = RequestMethod.GET)
	  public String addQuiz(Model model){
		  List<Category> categories = categoryDAO.findAll();
		  model.addAttribute("categories", categories);
		  return "addQuiz";
	  }
	  
	  @RequestMapping(value = "/insertQuiz", method = RequestMethod.POST)
	  public String insertQuiz(@ModelAttribute(value="quiz") Quiz quiz, @RequestParam long categoryid, @RequestParam String tags, BindingResult result){
		  //long catid = category.getCatid();
		  String tagvalues[] = tags.split(",");
		  Tag tag = new Tag();
		  int i=0;
		  quiz.setCatid(categoryid);
		  quizDAO.addQuiz(quiz); 
		  for(i=0;i<tagvalues.length;i++){
			  Tag tagOld = tagDAO.find(tagvalues[i]);
			  logger.info("Welcome {}.", tagOld);
			  if(tagOld == null){
				  tag.setTag(tagvalues[i]);
				  tagDAO.addTag(tag);
				  quizTagDAO.addQuizTag(quiz.getQzid(), tag.getTagid());
			  }
			  else{
				  
				  quizTagDAO.addQuizTag(quiz.getQzid(), tagOld.getTagid());
			  }
		  }
		  return "redirect:/";
	  }
	  
	  @RequestMapping(value = "/editQuiz", method = RequestMethod.GET)
	  public String editQuiz(Model model, @RequestParam long qzid){
		  Quiz quiz = quizDAO.find(qzid);
		  List<Category> categories = categoryDAO.findAll();
		  model.addAttribute("categories", categories);
		  model.addAttribute("quiz", quiz);
		  return "editQuiz";
	  }
	  
	  @RequestMapping(value = "/updateQuiz", method = RequestMethod.POST)
	  public String updateQuiz(@ModelAttribute(value="quiz") Quiz quiz, @RequestParam long qzid, @RequestParam long categoryid, @RequestParam String tags, BindingResult result){
		  String tagvalues[] = tags.split(",");
		  Tag tag = new Tag();
		  int i=0;
		  quiz.setCatid(categoryid);
		  quiz.setQzid(qzid);
		  quizDAO.updateQuiz(quiz); 
		  for(i=0;i<tagvalues.length;i++){
			  Tag tagOld = tagDAO.find(tagvalues[i]);
			  if(tagOld == null){
				  tag.setTag(tagvalues[i]);
				  tagDAO.addTag(tag);//May problema pa sa update
			  }
			  
		  }
		  return "redirect:/questionList?quizid="+quiz.getQzid();
	  }
	  
	  
	  @RequestMapping(value = "/deleteQuiz", method = RequestMethod.GET)
	  public String deleteQuiz(Model model, @RequestParam long qzid){
		  quizDAO.deleteQuiz(qzid);
		  return "redirect:/";
	  }

}
