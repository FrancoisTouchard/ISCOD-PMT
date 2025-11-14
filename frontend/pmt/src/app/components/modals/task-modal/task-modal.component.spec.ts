import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskModalComponent } from './task-modal.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Priority } from '../../../models/priority.enum';
import { TaskStatus } from '../../../models/taskStatus.enum';

describe('TaskModalComponent', () => {
  let component: TaskModalComponent;
  let fixture: ComponentFixture<TaskModalComponent>;

  const mockProject = {
    contributors: [
      { id: { idUser: '1' }, userName: 'Spip', userEmail: 'spip@mail.com' },
      {
        id: { idUser: '2' },
        userName: 'Fantasio',
        userEmail: 'fantasio@mail.com',
      },
    ],
  };

  const mockTask = {
    id: 'yeah',
    name: 'Test task',
    description: 'Desc',
    dueDate: '2025-21-01',
    endDate: '2025-11-01',
    status: TaskStatus.TODO,
    priority: Priority.MEDIUM,
    assignments: [{ id: { userId: '1', taskId: '2', projectId: '3' } }],
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskModalComponent],
      providers: [provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(TaskModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ngOnChanges + initializeForm (mode create)

  it('should initialize form in create mode', () => {
    component.isOpen = true;
    component.mode = 'create';

    component.ngOnChanges({
      isOpen: {
        currentValue: true,
        previousValue: false,
        firstChange: true,
        isFirstChange: () => true,
      },
      mode: {
        currentValue: 'create',
        previousValue: 'view',
        firstChange: true,
        isFirstChange: () => true,
      },
    });

    expect(component.isEditMode).toBeTrue();
    expect(component.taskForm.value.name).toBeNull();
    expect(component.taskForm.value.priority).toBe(Priority.MEDIUM);
  });

  // initializeForm (mode edit)

  it('should patch form values in edit mode', () => {
    component.isOpen = true;
    component.mode = 'edit';
    component.task = mockTask;

    component.ngOnChanges({
      isOpen: {
        currentValue: true,
        previousValue: false,
        firstChange: true,
        isFirstChange: () => true,
      },
    });

    expect(component.taskForm.value.name).toBe('Test task');
    expect(component.taskForm.value.assigneeIds).toEqual(['1']);
  });

  // title getter

  it('should return correct title', () => {
    component.mode = 'create';
    expect(component.title).toBe('Créer une tâche');

    component.mode = 'edit';
    expect(component.title).toBe('Modifier la tâche');

    component.mode = 'view';
    expect(component.title).toBe('Détails de la tâche');
  });

  // onAssigneeToggle

  it('should add assignee ID when checked', () => {
    component.assigneeIds.setValue([]);

    component.onAssigneeToggle('1', true);

    expect(component.assigneeIds.value).toEqual(['1']);
  });

  it('should remove assignee ID when unchecked', () => {
    component.assigneeIds.setValue(['1', '2']);

    component.onAssigneeToggle('1', false);

    expect(component.assigneeIds.value).toEqual(['2']);
  });

  // getAssigneesNames

  it('should return assignees names', () => {
    component.project = mockProject as any;
    component.task = mockTask as any;

    const names = component.getAssigneesNames();

    expect(names).toBe('Spip');
  });

  // enableEditMode

  it('should enable edit mode', () => {
    component.mode = 'view';
    component.isEditMode = false;

    component.enableEditMode();

    expect(component.isEditMode).toBeTrue();
    expect(component.mode).toBe('edit');
  });

  // cancelEdit

  it('should close modal when cancelling create', () => {
    spyOn(component, 'close');

    component.mode = 'create';
    component.cancelEdit();

    expect(component.close).toHaveBeenCalled();
  });

  it('should revert to view mode when cancelling edit', () => {
    component.mode = 'edit';
    component.task = mockTask as any;
    spyOn(component, 'initializeForm');

    component.cancelEdit();

    expect(component.isEditMode).toBeFalse();
    expect(component.mode).toBe('view');
    expect(component.initializeForm).toHaveBeenCalled();
  });

  // onSubmit

  it('should emit saved event in create mode and close modal', () => {
    component.mode = 'create';
    component.isEditMode = true;

    const spySaved = spyOn(component.saved, 'emit');
    const spyClose = spyOn(component, 'close');

    component.taskForm.setValue({
      name: 'Task',
      description: 'desc',
      dueDate: '2025-01-01',
      endDate: null,
      priority: Priority.HIGH,
      status: TaskStatus.TODO,
      assigneeIds: ['1'],
    });

    component.onSubmit();

    expect(spySaved).toHaveBeenCalled();
    expect(spyClose).toHaveBeenCalled();
  });

  it('should go back to view mode after editing', () => {
    component.mode = 'edit';
    component.isEditMode = true;

    const spySaved = spyOn(component.saved, 'emit');

    component.taskForm.setValue({
      name: 'Task',
      description: 'desc',
      dueDate: '2025-01-01',
      endDate: null,
      priority: Priority.LOW,
      status: TaskStatus.IN_PROGRESS,
      assigneeIds: [],
    });

    component.onSubmit();

    expect(spySaved).toHaveBeenCalled();
    expect(component.isEditMode).toBeFalse();
    expect(component.mode).toBe('view');
  });

  // close()

  it('should reset form and emit closed event', () => {
    const spyClosed = spyOn(component.closed, 'emit');

    component.taskForm.patchValue({ name: 'abc' });

    component.close();

    expect(component.taskForm.value.name).toBeNull();
    expect(spyClosed).toHaveBeenCalled();
    expect(component.mode).toBe('view');
  });
});
