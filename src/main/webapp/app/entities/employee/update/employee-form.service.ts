import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IEmployee, NewEmployee } from '../employee.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEmployee for edit and NewEmployeeFormGroupInput for create.
 */
type EmployeeFormGroupInput = IEmployee | PartialWithRequiredKeyOf<NewEmployee>;

type EmployeeFormDefaults = Pick<NewEmployee, 'id' | 'isActive'>;

type EmployeeFormGroupContent = {
  id: FormControl<IEmployee['id'] | NewEmployee['id']>;
  employeeCode: FormControl<IEmployee['employeeCode']>;
  username: FormControl<IEmployee['username']>;
  hashPassword: FormControl<IEmployee['hashPassword']>;
  name: FormControl<IEmployee['name']>;
  phone: FormControl<IEmployee['phone']>;
  email: FormControl<IEmployee['email']>;
  note: FormControl<IEmployee['note']>;
  status: FormControl<IEmployee['status']>;
  isActive: FormControl<IEmployee['isActive']>;
};

export type EmployeeFormGroup = FormGroup<EmployeeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EmployeeFormService {
  createEmployeeFormGroup(employee: EmployeeFormGroupInput = { id: null }): EmployeeFormGroup {
    const employeeRawValue = {
      ...this.getFormDefaults(),
      ...employee,
    };
    return new FormGroup<EmployeeFormGroupContent>({
      id: new FormControl(
        { value: employeeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      employeeCode: new FormControl(employeeRawValue.employeeCode),
      username: new FormControl(employeeRawValue.username),
      hashPassword: new FormControl(employeeRawValue.hashPassword),
      name: new FormControl(employeeRawValue.name),
      phone: new FormControl(employeeRawValue.phone),
      email: new FormControl(employeeRawValue.email),
      note: new FormControl(employeeRawValue.note),
      status: new FormControl(employeeRawValue.status),
      isActive: new FormControl(employeeRawValue.isActive),
    });
  }

  getEmployee(form: EmployeeFormGroup): IEmployee | NewEmployee {
    return form.getRawValue() as IEmployee | NewEmployee;
  }

  resetForm(form: EmployeeFormGroup, employee: EmployeeFormGroupInput): void {
    const employeeRawValue = { ...this.getFormDefaults(), ...employee };
    form.reset(
      {
        ...employeeRawValue,
        id: { value: employeeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): EmployeeFormDefaults {
    return {
      id: null,
      isActive: false,
    };
  }
}
