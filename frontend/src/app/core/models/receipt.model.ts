export interface Receipt {
  id?: number;
  fileName: string;
  filePath: string;
  fileType: string;
  fileSize?: number;
  uploadUrl?: string;
}
