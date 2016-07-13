package com.rui.pro1.common.utils.web;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.rui.pro1.common.bean.EmailBean;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 信邮发送工具帮助
 * 
 * @author ruiliang
 * @date 2016/7/13
 *
 */
public class EmailUtil {

	protected static Logger logger = LoggerFactory.getLogger(EmailUtil.class);
	public final static String hostName = "smtp.163.com";// 协议
	public final static String from = "rui_dev@163.com";// 发件人
	public final static String userName = "rui_dev";// 登陆名
	public final static String password = "ruidev123456";// smt协议 密码
	public final static int smtpPort = 25;//
	public final static String sslSmtpPort = "465";//

	public static Email getDefaultEmailConfig(Email email)
			throws EmailException {
		// Email email = new SimpleEmail();
		email.setHostName(hostName);
		email.setSslSmtpPort(sslSmtpPort);
		email.setAuthenticator(new DefaultAuthenticator(userName, password));// @163.com
		email.setSSLOnConnect(true);
		email.setFrom(from);
		email.setCharset("utf-8");

		return email;
	}

	public static void main(String[] args) throws EmailException {
		Email email = new SimpleEmail();
		email.setHostName(hostName);
		email.setSslSmtpPort(sslSmtpPort);
		email.setAuthenticator(new DefaultAuthenticator(userName, password));// @163.com
		email.setSSLOnConnect(true);
		// 发送人
		email.setFrom(from);// 设置字段的电子邮件使用指定的地址。电子邮件
		email.setSubject("送行-梁实秋");

		email.setMsg("发邮件simple test");
		// 接收人
		email.addTo("rui_dev@126.com", "toName");// 382453602@qq.com
													// rui_dev@126.com
		email.send();

		System.out.println("ok>>>");
	}

	/**
	 * 发送简单文本邮件
	 * 
	 * @param context邮件文本
	 * @param subject
	 *            主题
	 * @param toEmail
	 *            发送给谁的邮件地址
	 * @param toEmailName
	 *            名称
	 * @throws EmailException
	 */
	public static void sendContextEmail(String context, String subject,
			String toEmail, String toEmailName) throws EmailException {
		if (StringUtils.isBlank(context)) {
			throw new EmailException("邮件文本不能为空");
		}
		if (StringUtils.isBlank(toEmail)) {
			throw new EmailException("邮件接收人不能为空");
		}
		if (StringUtils.isBlank(subject)) {
			throw new EmailException("邮件主题不能为空");
		}

		try {
			Email email = getDefaultEmailConfig(new SimpleEmail());
			email.setSubject(subject);
			email.setMsg(toEmail);

			if (StringUtils.isBlank(toEmailName)) {
				// 接收人
				email.addTo(toEmail);
			} else {
				// 接收人
				email.addTo(toEmail, toEmailName);// 1067165280@qq.com
													// rui_dev@126.com
			}

			email.send();
		} catch (EmailException e) {
			logger.error("邮件发送异常!{}" + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * 发送简单文本邮件
	 * 
	 * @param email
	 *            bean
	 * @throws EmailException
	 */
	public static void sendContextEmail(EmailBean email) throws EmailException {

		if (StringUtils.isBlank(email.getContext())) {
			throw new EmailException("邮件文本不能为空");
		}
		if (StringUtils.isBlank(email.getToEmail())) {
			throw new EmailException("邮件接收人不能为空");
		}
		if (StringUtils.isBlank(email.getSubject())) {
			throw new EmailException("邮件主题不能为空");
		}

		sendContextEmail(email.getContext(), email.getSubject(),
				email.getToEmail(), email.getToEmailName());

	}

	/**
	 * 发送ftl模板邮件
	 * 
	 * @param map
	 *            邮件内容 对应ftl模板数据
	 * @param subject
	 *            主题
	 * @param toEmail
	 *            发送给谁的邮件地址
	 * @param toEmailName
	 *            名称
	 * @throws EmailException
	 */
	public static void sendFtlEmail(Map<String, Object> map, String ftlName,
			String subject, String toEmail, String toEmailName)
			throws EmailException {
		if (StringUtils.isBlank(toEmail)) {
			throw new EmailException("邮件接收人不能为空");
		}
		if (StringUtils.isBlank(subject)) {
			throw new EmailException("邮件主题不能为空");
		}

		try {
			FreeMarkerConfigurer configurer = FreeMarkerUtil
					.getFreeMarkerConfigurer();
			Template tpl = configurer.getConfiguration().getTemplate(ftlName);
			// if (map==null||map.size()<=0) {
			// }
			String ftlContext = FreeMarkerTemplateUtils
					.processTemplateIntoString(tpl, map);
			System.out.println(ftlContext);
			HtmlEmail email = (HtmlEmail) getDefaultEmailConfig(new HtmlEmail());
			// MimeMultipart multipart = new MimeMultipart("related");
			email.setHtmlMsg(ftlContext);
			// email.setTextMsg("xx");//文本消息的一部分。如果这将被用作替代文本 电子邮件客户端不支持HTML消息。
			email.setSubject(subject);
			//email.setMsg(toEmail);
			if (StringUtils.isBlank(toEmailName)) {
				// 接收人
				email.addTo(toEmail);
			} else {
				// 接收人
				email.addTo(toEmail, toEmailName);// 1067165280@qq.com
													// rui_dev@126.com
			}

			email.send();
		} catch (IOException e) {
			logger.error("freeMaraker模板加载异常!{}" + e.getMessage());
			e.printStackTrace();
		} catch (EmailException e) {
			logger.error("邮件发送异常!{}" + e.getMessage());
			e.printStackTrace();
		} catch (TemplateException e) {
			logger.error("processTemplateIntoString处理异常!{}" + e.getMessage());
			e.printStackTrace();
		}

	}

}
