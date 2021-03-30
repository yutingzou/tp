package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.appointment.AppointmentDateTime;
import seedu.address.model.appointment.DateViewPredicate;
import seedu.address.model.budget.Budget;
import seedu.address.model.event.Event;
import seedu.address.model.filter.AppointmentFilter;
import seedu.address.model.filter.TutorFilter;
import seedu.address.model.grade.Grade;
import seedu.address.model.tutor.Name;
import seedu.address.model.tutor.Tutor;
import seedu.address.model.schedule.ReadOnlyScheduleTracker;
import seedu.address.model.schedule.Schedule;
import seedu.address.model.schedule.ScheduleTracker;
import seedu.address.model.util.SampleDataUtil;

/**
 * Represents the in-memory model of the tutor book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final TutorBook tutorBook;
    private final AppointmentBook appointmentBook;
    private final GradeBook gradeBook;
    private final ScheduleTracker scheduleTracker;

    private final UserPrefs userPrefs;

    private final TutorFilter tutorFilter;
    private final AppointmentFilter appointmentFilter;
    private final FilteredList<Tutor> filteredTutors;
    private final FilteredList<Appointment> filteredAppointment;
    private final FilteredList<Grade> filteredGrades;
    private final FilteredList<Schedule> filteredSchedule;

    private final BudgetBook budgetBook;

    /**
     * Initializes a ModelManager with the given TutorBook, AppointmentBook, BudgetBook, GradeBook and userPrefs.
     */
    public ModelManager(ReadOnlyTutorBook tutorBook, ReadOnlyUserPrefs userPrefs,
                        ReadOnlyAppointmentBook appointmentBook,
                        BudgetBook budgetBook, ReadOnlyGradeBook gradeBook) {
        super();
        requireAllNonNull(tutorBook, appointmentBook, userPrefs, budgetBook);

        logger.fine("Initializing with tutor book: " + tutorBook + " and user prefs " + userPrefs);

        this.tutorBook = new TutorBook(tutorBook);
        this.appointmentBook = new AppointmentBook(appointmentBook);
        this.scheduleTracker = new ScheduleTracker(SampleDataUtil.getSampleScheduleTracker());
        this.gradeBook = new GradeBook(gradeBook);
        this.budgetBook = new BudgetBook(budgetBook);
        this.userPrefs = new UserPrefs(userPrefs);

        this.tutorFilter = new TutorFilter();
        this.appointmentFilter = new AppointmentFilter();
        filteredTutors = new FilteredList<>(this.tutorBook.getTutorList());
        filteredAppointment = new FilteredList<>(this.appointmentBook.getAppointmentList());
        filteredGrades = new FilteredList<>(this.gradeBook.getGradeList());
        this.filteredSchedule = new FilteredList<>(this.scheduleTracker.getScheduleList());
    }

    /**
     * Default constructor without params. Initializes with empty books.
     */
    public ModelManager() {
        this(new TutorBook(), new UserPrefs(), new AppointmentBook(),
                new BudgetBook(), new GradeBook());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getTutorBookFilePath() {
        return userPrefs.getTutorBookFilePath();
    }

    @Override
    public void setTutorBookFilePath(Path tutorBookFilePath) {
        requireNonNull(tutorBookFilePath);
        userPrefs.setTutorBookFilePath(tutorBookFilePath);
    }

    @Override
    public Path getAppointmentBookFilePath() {
        return userPrefs.getAppointmentBookFilePath();
    }

    @Override
    public void setAppointmentBookFilePath(Path appointmentBookFilePath) {
        requireNonNull(appointmentBookFilePath);
        userPrefs.setAppointmentBookFilePath(appointmentBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public ReadOnlyTutorBook getTutorBook() {
        return tutorBook;
    }

    @Override
    public void setTutorBook(ReadOnlyTutorBook tutorBook) {
        this.tutorBook.resetData(tutorBook);
    }

    @Override
    public boolean hasTutor(Tutor tutor) {
        requireNonNull(tutor);
        return tutorBook.hasTutor(tutor);
    }

    @Override
    public boolean hasTutorByName(Name name) {
        return tutorBook.containsTutorByName(name);
    }

    @Override
    public void deleteTutor(Tutor target) {
        tutorBook.removeTutor(target);
    }

    @Override
    public void addTutor(Tutor tutor) {
        tutorBook.addTutor(tutor);
        updateFilteredTutorList(PREDICATE_SHOW_ALL_TUTORS);
    }

    @Override
    public void setTutor(Tutor target, Tutor editedTutor) {
        requireAllNonNull(target, editedTutor);

        tutorBook.setTutor(target, editedTutor);
    }

    //=========== AppointmentBook=============================================================================

    @Override
    public ReadOnlyAppointmentBook getAppointmentBook() {
        return appointmentBook;
    }

    public void setAppointmentBook(ReadOnlyAppointmentBook appointmentBook) {
        this.appointmentBook.resetData(appointmentBook);
    }

    @Override
    public boolean hasAppointmentContainingTutor(Name name) {
        return this.appointmentBook.hasAppointmentContainingTutor(name);
    }

    //=========== Filtered Tutor List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Tutor} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Tutor> getFilteredTutorList() {
        return filteredTutors;
    }

    /**
     * Returns an unmodifiable view of the list of {@code Appointment} backed by the internal list of
     * {@code versionedAppointmentBook}
     */
    @Override
    public ObservableList<Appointment> getFilteredAppointmentList() {
        return filteredAppointment;
    }

    /**
     * Updates the filter of the filtered tutor list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    @Override
    public void updateFilteredTutorList(Predicate<Tutor> predicate) {
        requireNonNull(predicate);
        filteredTutors.setPredicate(predicate);
    }

    //=========== AppointmentList ============================================================================

    /**
     * Updates the filter of the filtered appointment list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    @Override
    public void updateFilteredAppointmentList(Predicate<Appointment> predicate) {
        requireNonNull(predicate);
        filteredAppointment.setPredicate(predicate);
    }



    /**
     * Checks if Appointment exists in appointment list.
     *
     * @param appointment Appointment to check
     * @return True if appointment is already in appointment list
     */
    @Override
    public boolean hasAppointment(Appointment appointment) {
        return appointmentBook.hasAppointment(appointment);
    }

    /**
     * @param appointment Appointment to add (appointment must not already exist)
     */
    @Override
    public void addAppointment(Appointment appointment) {
        appointmentBook.addAppointment(appointment);
    }

    /**
     * Removes appointment from appointment list.
     *
     * @param appointment Appointment to remove must be present
     */
    @Override
    public void removeAppointment(Appointment appointment) {
        appointmentBook.removeAppointment(appointment);
    }

    @Override
    public void setAppointment(Appointment target, Appointment editedAppointment) {
        requireAllNonNull(target, editedAppointment);

        appointmentBook.setAppointment(target, editedAppointment);
    }

    /**
     * Method that removes appointment based on index
     *
     * @param indexToRemove Index of appointment to remove
     */
    @Override
    public void removeAppointmentIndex(int indexToRemove) {
        appointmentBook.removeAppointment(indexToRemove);
    }

    /**
     * Checks if {@code AppointmentDateTime} exists in the appointment list.
     *
     * @param appointmentDateTime Appointment DateTime to be checked
     * @return true if Appointment DateTime exists in the appointment list
     */
    @Override
    public boolean hasAppointmentDateTime(AppointmentDateTime appointmentDateTime) {
        return !filteredAppointment.filtered(new DateViewPredicate(appointmentDateTime)).isEmpty();
    }

    //============== Budget ============================================================

    /**
     * Getter method to retrieve budget book.
     *
     * @return Budget book.
     */
    public BudgetBook getBudgetBook() {
        return budgetBook;
    }

    /**
     * @return True is budget already exists.
     */
    @Override
    public boolean hasBudget() {
        return budgetBook.hasBudget();
    }

    /**
     * @param budget Budget to verify whether present.
     */
    @Override
    public boolean hasBudget(Budget budget) {
        return budgetBook.hasBudget();
    }

    /**
     * Adds budget to budget book. Budget must not be present.
     *
     * @param budget Budget to add.
     */
    @Override
    public void addBudget(Budget budget) {
        budgetBook.addBudget(budget);
    }

    /**
     * Edits an already present {@code budget}.
     *
     * @param budget Budget to update to.
     */
    @Override
    public void editBudget(Budget budget) {
        budgetBook.setBudget(budget);
    }

    /**
     * Removes an already existing budget.
     */
    @Override
    public void deleteBudget() {
        budgetBook.deleteBudget();
    }

    //=========== GradeList ============================================================================

    /**
     * Updates the filter of the filtered appointment list to filter by the given {@code predicate}.
     *
     * @throws NullPointerException if {@code predicate} is null.
     */
    @Override
    public void updateFilteredGradeList(Predicate<Grade> predicate) {
        requireNonNull(predicate);
        filteredGrades.setPredicate(predicate);
    }

    public ReadOnlyGradeBook getGradeBook() {
        return gradeBook;

    }

    public void setGradeBook(ReadOnlyGradeBook readOnlyGradeBook) {
        this.gradeBook.resetData(readOnlyGradeBook);
    }

    /**
     * @return File path of Grade Book data file
     */
    public Path getGradeBookFilePath() {
        return userPrefs.getGradeBookFilePath();
    }

    /**
     * Sets grade book file path.
     *
     * @param gradeBookFilePath To be supplied by user
     */
    public void setGradeBookFilePath(Path gradeBookFilePath) {
        requireNonNull(gradeBookFilePath);
        userPrefs.setGradeBookFilePath(gradeBookFilePath);
    }

    /**
     * Returns true if a grade with the same identity as {@code grade} exists in the grade book.
     */
    public boolean hasGrade(Grade grade) {
        requireNonNull(grade);
        return gradeBook.hasGrade(grade);
    }

    /**
     * Deletes the given grade.
     * The grade must exist in the grade book.
     */
    public void deleteGrade(Grade target) {
        gradeBook.removeGrade(target);
    }

    /**
     * Adds the given grade.
     * {@code grade} must not already exist in the grade book.
     */
    public void addGrade(Grade grade) {
        gradeBook.addGrade(grade);
    }

    /**
     * Replaces the given grade {@code target} with {@code editedGrade}.
     * {@code target} must exist in the grade book.
     * The grade identity of {@code editedGrade} must not be the same as another existing grade in the grade book.
     */
    public void setGrade(Grade target, Grade editedGrade) {
        requireAllNonNull(target, editedGrade);
        gradeBook.setGrade(target, editedGrade);
    }

    /**
     * Method that removes grade based on index
     *
     * @param indexToRemove index of grade to remove
     */
    @Override
    public void removeGradeIndex(int indexToRemove) {
        gradeBook.removeGrade(indexToRemove);
    }

    /**
     * Returns an unmodifiable view of the filtered grade list
     */
    public ObservableList<Grade> getFilteredGradeList() {
        return filteredGrades;
    }

    //=========== TutorFilter =====================================================================
    @Override
    public boolean hasTutorFilter(TutorFilter tutorFilter) {
        return this.tutorFilter.has(tutorFilter);
    }

    @Override
    public void addTutorFilter(TutorFilter tutorFilter) {
        this.tutorFilter.add(tutorFilter);

        // Required workaround for bug where filtered list would not trigger update
        this.updateFilteredTutorList(PREDICATE_SHOW_ALL_TUTORS);

        this.updateFilteredTutorList(this.tutorFilter);
    }

    @Override
    public void removeTutorFilter(TutorFilter tutorFilter) {
        this.tutorFilter.remove(tutorFilter);

        // Required workaround for bug where filtered list would not trigger update
        this.updateFilteredTutorList(PREDICATE_SHOW_ALL_TUTORS);

        this.updateFilteredTutorList(this.tutorFilter);
    }

    @Override
    public ObservableList<String> getTutorFilterStringList() {
        return this.tutorFilter.asUnmodifiableObservableList();
    }

    //=========== AppointmentFilter =====================================================================
    @Override
    public boolean hasAppointmentFilter(AppointmentFilter appointmentFilter) {
        return this.appointmentFilter.has(appointmentFilter);
    }

    @Override
    public void addAppointmentFilter(AppointmentFilter appointmentFilter) {
        this.appointmentFilter.add(appointmentFilter);

        // Required workaround for bug where filtered list would not trigger update
        this.updateFilteredAppointmentList(PREDICATE_SHOW_ALL_APPOINTMENT);

        this.updateFilteredAppointmentList(this.appointmentFilter);
    }

    @Override
    public void removeAppointmentFilter(AppointmentFilter appointmentFilter) {
        this.appointmentFilter.remove(appointmentFilter);

        // Required workaround for bug where filtered list would not trigger update
        this.updateFilteredAppointmentList(PREDICATE_SHOW_ALL_APPOINTMENT);

        this.updateFilteredAppointmentList(this.appointmentFilter);
    }

    @Override
    public ObservableList<String> getAppointmentFilterStringList() {
        return this.appointmentFilter.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return tutorBook.equals(other.tutorBook)
                && userPrefs.equals(other.userPrefs)
                && filteredTutors.equals(other.filteredTutors)
                && tutorFilter.equals(other.tutorFilter)
                && appointmentBook.equals(other.appointmentBook)
                && budgetBook.equals(other.budgetBook)
                && filteredSchedule.equals(other.filteredSchedule);
    }

    @Override
    public ReadOnlyScheduleTracker getScheduleTracker() {
        return scheduleTracker;
    }

    @Override
    public void setScheduleTracker(ReadOnlyScheduleTracker scheduleTracker) {
        this.scheduleTracker.resetData(scheduleTracker);
    }

    @Override
    public ObservableList<Schedule> getFilteredScheduleList() {
        return filteredSchedule;
    }

    @Override
    public void updateFilteredScheduleList(Predicate<Schedule> predicate) {
        requireNonNull(predicate);
        filteredSchedule.setPredicate(predicate);
    }

    @Override
    public boolean hasSchedule(Schedule schedule) {
        return scheduleTracker.hasSchedule(schedule);
    }

    @Override
    public void addSchedule(Schedule schedule) {
        scheduleTracker.addSchedule(schedule);
    }

    @Override
    public void deleteSchedule(Schedule schedule) {
        scheduleTracker.removeSchedule(schedule);
    }

    @Override
    public void setSchedule(Schedule target, Schedule editedSchedule) {
        scheduleTracker.setSchedule(target, editedSchedule);
    }

    @Override
    public ObservableList<Event> getFilteredEventList() {
        ObservableList<Event> filteredEvents = FXCollections.observableArrayList();
        filteredEvents.addAll(filteredAppointment);
        filteredEvents.addAll(filteredSchedule);
        return filteredEvents;
    }
}
