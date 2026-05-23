package com.example.specdriven.orders.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.specdriven.orders.domain.Order;
import com.example.specdriven.orders.domain.OrderRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.browserless.SpringBrowserlessTest;

@SpringBootTest
class OrderFlowersViewTest extends SpringBrowserlessTest {

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void clearOrders() {
        orderRepository.deleteAll();
    }

    @Test
    void mainFlow_persistsOrderAndNavigatesToConfirmation() {
        navigate(OrderFlowersView.class);

        test(qty("rose", "red")).setValue(3);
        test(qty("tulip", "yellow")).setValue(2);
        test(name()).setValue("Alice");
        test(phone()).setValue("555-1234");
        test(confirmButton()).click();

        assertInstanceOf(OrderConfirmationView.class, getCurrentView(),
                "Main Flow step 8: navigates to confirmation view");

        assertEquals(1, orderRepository.count(), "Postcondition: order is persisted");
        Order saved = orderRepository.findAll().get(0);
        assertEquals("Alice", saved.getCustomerName());
        assertEquals("555-1234", saved.getPhoneNumber());
        assertEquals(2, saved.getLines().size());
    }

    @Test
    void af1_noFlowersSelected_showsNotificationAndStaysOnForm() {
        navigate(OrderFlowersView.class);

        test(name()).setValue("Alice");
        test(phone()).setValue("555-1234");
        test(confirmButton()).click();

        assertInstanceOf(OrderFlowersView.class, getCurrentView(),
                "AF-1: stays on the form when no flowers are selected");

        Notification notification = $(Notification.class).single();
        assertEquals("Select at least one flower", test(notification).getText());

        assertEquals(0, orderRepository.count(),
                "Failure postcondition: no order persisted");
    }

    @Test
    void af2_blankName_marksNameInvalidAndDoesNotPersist() {
        navigate(OrderFlowersView.class);

        test(qty("rose", "red")).setValue(1);
        test(phone()).setValue("555-1234");
        test(confirmButton()).click();

        assertTrue(name().isInvalid(), "AF-2: name field is marked invalid");
        assertFalse(phone().isInvalid(), "phone is OK and stays valid");
        assertInstanceOf(OrderFlowersView.class, getCurrentView(),
                "Stays on form when name is missing");
        assertEquals(0, orderRepository.count());
    }

    @Test
    void af2_blankPhone_marksPhoneInvalidAndDoesNotPersist() {
        navigate(OrderFlowersView.class);

        test(qty("rose", "red")).setValue(1);
        test(name()).setValue("Alice");
        test(confirmButton()).click();

        assertTrue(phone().isInvalid(), "AF-2: phone field is marked invalid");
        assertFalse(name().isInvalid());
        assertInstanceOf(OrderFlowersView.class, getCurrentView());
        assertEquals(0, orderRepository.count());
    }

    @Test
    void br05_acceptsLargeQuantityWithoutUpperBound() {
        navigate(OrderFlowersView.class);

        test(qty("lily", "orange")).setValue(1234);
        test(name()).setValue("Alice");
        test(phone()).setValue("555");
        test(confirmButton()).click();

        assertInstanceOf(OrderConfirmationView.class, getCurrentView());
        Order saved = orderRepository.findAll().get(0);
        assertEquals(1234, saved.getLines().get(0).getQuantity());
    }

    private IntegerField qty(String flower, String color) {
        return $(IntegerField.class).id("qty-" + flower + "-" + color);
    }

    private TextField name() {
        return $(TextField.class).id("name");
    }

    private TextField phone() {
        return $(TextField.class).id("phone");
    }

    private Button confirmButton() {
        return $(Button.class).id("confirm-order");
    }
}
