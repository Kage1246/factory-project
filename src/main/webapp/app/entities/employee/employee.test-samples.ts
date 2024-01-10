import { IEmployee, NewEmployee } from './employee.model';

export const sampleWithRequiredData: IEmployee = {
  id: 25482,
};

export const sampleWithPartialData: IEmployee = {
  id: 17820,
  employeeCode: 'incandescence caring',
  name: 'clear if excepting',
  email: 'Genesis.Yundt44@hotmail.com',
};

export const sampleWithFullData: IEmployee = {
  id: 25771,
  employeeCode: 'aide',
  username: 'discolour rosy whoa',
  hashPassword: 'idealize because',
  name: 'yellowish signet',
  phone: '233-243-7874 x2086',
  email: 'Adam91@gmail.com',
  note: 'actualize',
  status: 1774,
  isActive: true,
};

export const sampleWithNewData: NewEmployee = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
