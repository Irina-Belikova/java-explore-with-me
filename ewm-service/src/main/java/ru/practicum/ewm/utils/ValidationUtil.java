package ru.practicum.ewm.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.dto.AdminStateAction;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.StatusState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.BadParameterException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.user.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.model.Request;
import ru.practicum.ewm.user.model.RequestStatus;
import ru.practicum.ewm.user.repository.RequestRepository;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ValidationUtil {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CompilationRepository compilationRepository;

    public void validationForAddUser(NewUserRequest dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ValidationException(String.format("Пользователь с таким email - %s уже существует.", dto.getEmail()));
        }
    }

    public void checkUserId(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователя с id - %d не существует.", userId));
        }
    }

    public void checkCategoryName(String name) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ValidationException(String.format("Категория с таким названием - %s уже существует.", name));
        }
    }

    public void validationForUpdateCategory(long catId, CategoryDto dto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категории с таким id - %d не существует.", catId)));
        if (categoryRepository.existsByNameIgnoreCase(dto.getName()) && !dto.getName().equals(category.getName())) {
            throw new ValidationException(String.format("Категория с таким названием - %s уже существует.", dto.getName()));
        }
    }

    public void checkCategoryId(long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Категории с таким id - %d не существует.", catId));
        }
    }

    public void validationForAddEvent(NewEventDto dto) {
        checkCategoryId(dto.getCategory());
        LocalDateTime date = DateFormatterUtil.parseStringToDate(dto.getEventDate());
        if (date.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadParameterException(String.format("Дата события должна быть не ранее %s",
                    DateFormatterUtil.formatDateToString(LocalDateTime.now().plusHours(2))));
        }
    }

    public void validationCategoryForDelete(long id) {
        checkCategoryId(id);
        if (eventRepository.existsByCategoryId(id)) {
            throw new ValidationException(String.format("Категория с таким id - %d связана с событиями.", id));
        }
    }

    public void checkEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("События с таким id - %d не существует.", eventId));
        }
    }

    public void validationGetUserEventById(long initiatorId, long eventId) {
        checkEventId(eventId);
        if (initiatorId != eventRepository.getInitiatorId(eventId)) {
            throw new ValidationException(String.format("Пользователь - %d не создавал событие - %d.", initiatorId, eventId));
        }
    }

    public void validationUpdateEventByInitiator(long initiatorId, long eventId, UpdateEventUserRequest dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("События с таким id - %d не существует.", eventId)));

        if (initiatorId != event.getInitiator().getId()) {
            throw new ValidationException(String.format("Пользователь - %d не создавал событие - %d.", initiatorId, eventId));
        }

        if (event.getState().equals(StatusState.PUBLISHED)) {
            throw new ValidationException("Нельзя изменить публикованное событие.");
        }

        if (dto.getEventDate() != null) {
            LocalDateTime date = DateFormatterUtil.parseStringToDate(dto.getEventDate());

            if (date.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadParameterException(String.format("Изменяемая дата события должна быть не ранее %s",
                        DateFormatterUtil.formatDateToString(LocalDateTime.now().plusHours(2))));
            }
        }

        if (dto.getCategory() != null) {
            checkCategoryId(dto.getCategory());
        }
    }

    public void validationUpdateEventByAdmin(long eventId, UpdateEventAdminRequest dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("События с таким id - %d не существует.", eventId)));

       if (dto.getEventDate() != null) {
           LocalDateTime newEventDate = DateFormatterUtil.parseStringToDate(dto.getEventDate());

           if (newEventDate.isBefore(LocalDateTime.now().plusHours(1))) {
               throw new BadParameterException(String.format("Изменяемая дата события должна быть не ранее %s",
                       DateFormatterUtil.formatDateToString(LocalDateTime.now().plusHours(1))));
           }
       }

       if (dto.getStateAction() != null) {
           if (dto.getStateAction().equals(AdminStateAction.PUBLISH_EVENT) && !event.getState().equals(StatusState.PENDING)) {
               throw new ValidationException("Опубликовать можно только событие в статусе ожидания публикации.");
           }

           if (dto.getStateAction().equals(AdminStateAction.REJECT_EVENT) && event.getState().equals(StatusState.PUBLISHED)) {
               throw new ValidationException("Нельзя отклонить опубликованное событие.");
           }
       }

        if (dto.getCategory() != null) {
            checkCategoryId(dto.getCategory());
        }
    }

    public void validationAddRequest(long requesterId, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("События с таким id - %d не существует.", eventId)));

        if (requestRepository.existsByRequesterId(requesterId)) {
            throw new ValidationException("Данный пользователь уже оставлял заявку на участие в событии.");
        }

        if (requesterId == event.getInitiator().getId()) {
            throw new ValidationException("Инициатор события не может оставлять заявку на участие в нём.");
        }

        if (!event.getState().equals(StatusState.PUBLISHED)) {
            throw new ValidationException("Нельзя оставить заявку на неопубликованное событие.");
        }

        int requestCount = requestRepository.getCountRequestsByEventId(eventId);
        if (requestCount == event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ValidationException("Лимит заявок на участие заполнен.");
        }
    }

    public void validationDeleteOwnRequest(long userId, long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запроса с id -%d не существует.", requestId)));

        if (userId != request.getRequester().getId()) {
            throw new ValidationException("Отменить можно только свой запрос.");
        }
    }

    public void validationUpdateRequestsByEventId(long initiatorId, long eventId, EventRequestStatusUpdateRequest dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("События с таким id - %d не существует.", eventId)));

        if (initiatorId != event.getInitiator().getId()) {
            throw new ValidationException(String.format("Пользователь - %d не создавал событие - %d.", initiatorId, eventId));
        }

        int requestCount = requestRepository.getCountRequestsByEventId(eventId);
        if (requestCount == event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ValidationException("Лимит заявок на участие заполнен.");
        }

        List<RequestStatus> statuses = requestRepository.getRequestStatusListForUpdate(dto.getRequestIds());
        if (statuses.contains(RequestStatus.CONFIRMED) || statuses.contains(RequestStatus.REJECTED) || statuses.contains(RequestStatus.CANCELED)) {
            throw new ValidationException("В списке заявок должны быть заявки только со статусом ожидания.");
        }
    }

    public void validationCompilationTitle(String title) {
        if (compilationRepository.existsByTitle(title)) {
            throw new ValidationException("Подборка с таким заголовком уже существует.");
        }
    }

    public void checkCompilationId(long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException(String.format("Подборки с таким id - %d не существует.", id));
        }
    }

    public void validationGetEventById(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("События с таким id - %d не существует.", eventId)));

        if (!event.getState().equals(StatusState.PUBLISHED)) {
            throw new NotFoundException("Данное событие не опубликовано.");
        }
    }

    public void validationGetEventsByParam(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new BadParameterException("Дата окончания выборки должна быть после даты начала.");
            }
        }
    }
}
