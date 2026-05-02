export class User {
  id!: number;
  username!: string;
  email!: string;
  firstName!: string;
  lastName!: string;
  fullName!: string;
  role!: string;
  isActive!: boolean;
  isVerifiedEmail!: boolean;
  phoneNumber!: string;
  createdAt!: Date;
  updatedAt!: Date;
  lastLogin!: Date;

  constructor(data: Partial<User>) {
    Object.assign(this, {
      ...data,
      createdAt: data.createdAt ? new Date(data.createdAt) : undefined,
      updatedAt: data.updatedAt ? new Date(data.updatedAt) : undefined,
      lastLogin: data.lastLogin ? new Date(data.lastLogin) : undefined,
    });
  }
}
