package com.osb.shopapp.email;

import com.osb.shopapp.common.AppConstant;
import com.osb.shopapp.order.Order;
import com.osb.shopapp.order.OrderItem;
import com.osb.shopapp.product.Product;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendActivationEmail(
            String to,
            String username,
            String activationUrl,
            String activationCode
    ) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", username);
        variables.put("activationUrl", activationUrl);
        variables.put("activationCode", activationCode);
        Context context = new Context();
        context.setVariables(variables);
        String template = templateEngine.process(AppConstant.USER_ACTIVATION_TEMPLATE, context);

        mimeMessageHelper.setFrom("resellmartmail@gmail.com");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject("ResellMart Order Confirmation");
        mimeMessageHelper.setText(template, true);

        mailSender.send(mimeMessage);
    }

    public void sendPurchaseConfirmationEmail(
            String to,
            Order order
    ) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );

        Map<String, Object> variables = new HashMap<>();
        variables.put("order", order);
        Context context = new Context();
        context.setVariables(variables);
        String template = templateEngine.process(AppConstant.USER_ACTIVATION_TEMPLATE, context);

        mimeMessageHelper.setFrom("resellmartmail@gmail.com");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject("ResellMart Order Confirmation");
        mimeMessageHelper.setText(template, true);

        mailSender.send(mimeMessage);
    }

    public void sendOrderCancellationEmail(
            String to,
            Order order,
            Product product,
            Integer requestedQuality
    ) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );

        Map<String, Object> variables = new HashMap<>();
        variables.put("order", order);
        variables.put("product", product);
        variables.put("requestedQuantity", requestedQuality);
        Context context = new Context();
        context.setVariables(variables);
        String template = templateEngine.process(AppConstant.USER_ACTIVATION_TEMPLATE, context);

        mimeMessageHelper.setFrom("resellmartmail@gmail.com");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject("ResellMart Order Cancellation");
        mimeMessageHelper.setText(template, true);

        mailSender.send(mimeMessage);
    }

    public void sendSaleConfirmationEmail(
            String to,
            String sellerName,
            Order order,
            List<OrderItem> orderItems,
            BigDecimal saleTotal
    ) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );

        Map<String, Object> variables = new HashMap<>();
        variables.put("order", order);
        variables.put("sellerName", sellerName);
        variables.put("orderItems", orderItems);
        variables.put("saleTotal", saleTotal);
        Context context = new Context();
        context.setVariables(variables);
        String template = templateEngine.process(AppConstant.USER_ACTIVATION_TEMPLATE, context);

        mimeMessageHelper.setFrom("resellmartmail@gmail.com");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject("ResellMart Sale Notification");
        mimeMessageHelper.setText(template, true);

        mailSender.send(mimeMessage);
    }

    public void sendShippingConfirmationEmail(
            String to,
            Order order
    ) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );

        Map<String, Object> variables = new HashMap<>();
        variables.put("order", order);
        Context context = new Context();
        context.setVariables(variables);
        String template = templateEngine.process(AppConstant.USER_ACTIVATION_TEMPLATE, context);

        mimeMessageHelper.setFrom("resellmartmail@gmail.com");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject("ResellMart Shipping Notification");
        mimeMessageHelper.setText(template, true);

        mailSender.send(mimeMessage);
    }
}
