/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-present IxorTalk CVBA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.ixortalk.mailing.service.send;

import com.ixortalk.mailing.service.config.IxortalkConfigProperties;
import org.apache.commons.codec.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.internet.MimeMessage;

import static java.util.Locale.forLanguageTag;

@Named
@EnableConfigurationProperties(IxortalkConfigProperties.class)
public class SendMailService {

    private final Logger log = LoggerFactory.getLogger(SendMailService.class);

    @Inject
    private JavaMailSenderImpl javaMailSender;

    @Inject
    private MessageSource messageSource;

    @Inject
    private SpringTemplateEngine templateEngine;

    @Inject
    private IxortalkConfigProperties ixortalkConfigProperties;

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart, isHtml, to, subject, content);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(ixortalkConfigProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent e-mail to '{}'", to);
        } catch (Exception e) {
            log.warn("E-mail could not be sent to '{}', exception is: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendTemplateEmail(SendMailVO sendMailVO) {
        log.debug("Sending templated ({}) e-mail to '{}'", sendMailVO.getTemplateName(), sendMailVO.getToEmail());
        Context context = new Context(forLanguageTag(sendMailVO.getLanguageTag()));
        context.setVariables(sendMailVO.getAdditionalVariables());
        String content = templateEngine.process(sendMailVO.getTemplateName(), context);
        String subject = messageSource.getMessage(sendMailVO.getSubjectI18NKey(), null, forLanguageTag(sendMailVO.getLanguageTag()));
        sendEmail(sendMailVO.getToEmail(), subject, content, false, true);
    }
}

