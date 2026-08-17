export interface User {
  id?: string | number;
  name: string;
  email: string;
  username?: string;
  phone?: string;
  role?: string;
  phoneNumber?: string;
  upiId?: string;
  profilePicture?: string;
  createdAt?: Date | string;
  updatedAt?: Date | string;
}
