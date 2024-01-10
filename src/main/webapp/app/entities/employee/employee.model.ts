export interface IEmployee {
  id: number;
  employeeCode?: string | null;
  username?: string | null;
  hashPassword?: string | null;
  name?: string | null;
  phone?: string | null;
  email?: string | null;
  note?: string | null;
  status?: number | null;
  isActive?: boolean | null;
}

export type NewEmployee = Omit<IEmployee, 'id'> & { id: null };
