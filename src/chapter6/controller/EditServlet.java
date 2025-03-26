package chapter6.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Message;
import chapter6.beans.User;
import chapter6.exception.NoRowsUpdatedRuntimeException;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/edit" })
public class EditServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public EditServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info(new Object() {}.getClass().getEnclosingClass().getName() +
		" : " + new Object() {}.getClass().getEnclosingMethod().getName());

		HttpSession session = request.getSession();
		User loginUser = (User) session.getAttribute("loginUser");
		List<String> errorMessages = new ArrayList<String>();


		int id;
		try {
			id = Integer.parseInt(request.getParameter("id"));
		} catch (NumberFormatException e){
			errorMessages.add("不正なパラメータが入力されました");
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}

		Message message = new MessageService().select(id);

		if(message == null || loginUser.getId() != message.getUserId()) {
			errorMessages.add("不正なパラメータが入力されました");
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}

		request.setAttribute("message", message);
		request.getRequestDispatcher("edit.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info(new Object() {}.getClass().getEnclosingClass().getName() +
		" : " + new Object() {}.getClass().getEnclosingMethod().getName());

		List<String> errorMessages = new ArrayList<String>();

		Message message = getMessage(request);
		String text = message.getText();
		if (isValid(text, errorMessages)) {
			try {
				new MessageService().update(message);
			} catch (NoRowsUpdatedRuntimeException e) {
				log.warning("他の人によって更新されています。最新のデータを表示しました。データを確認してください。");
				errorMessages.add("他の人によって更新されています。最新のデータを表示しました。データを確認してください。");
			}
		}

		if (errorMessages.size() != 0) {
			request.setAttribute("errorMessages", errorMessages);
			request.setAttribute("message", message);
			request.getRequestDispatcher("edit.jsp").forward(request, response);
			return;
		}

		response.sendRedirect("./");
	}

	private Message getMessage(HttpServletRequest request) throws IOException, ServletException {

		log.info(new Object() {}.getClass().getEnclosingClass().getName() +
		" : " + new Object() {}.getClass().getEnclosingMethod().getName());

		Message message = new Message();
		message.setId(Integer.parseInt(request.getParameter("id")));
		message.setText(request.getParameter("text"));
		return message;
	}

	private boolean isValid(String text, List<String> errorMessages) {

		log.info(new Object() {}.getClass().getEnclosingClass().getName() +
		" : " + new Object() {}.getClass().getEnclosingMethod().getName());

		if (StringUtils.isBlank(text)) {
			errorMessages.add("メッセージを入力してください");
		} else if (140 < text.length()) {
			errorMessages.add("140文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}

}
