package com.example.booking.observers;

import com.example.booking.domains.Booking;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class BookingAspect {
    private final List<BookingObserver> observers;

    @Around("@annotation(com.example.booking.observers.BookingOperation)")
    public Object handleOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (result instanceof Booking booking) {
            notifyObservers(booking);
        }
        return result;
    }

    private void notifyObservers(Booking booking) {
        observers.forEach(observer -> observer.onBooking(booking.getUser(), booking.getRoom(), booking));
    }
}
