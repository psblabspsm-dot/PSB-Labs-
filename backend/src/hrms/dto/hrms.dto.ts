import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsString, IsNotEmpty, IsOptional, IsEmail, IsNumber, IsDateString, IsEnum } from 'class-validator';

export class CreateEmployeeDto {
  @ApiProperty({ description: 'Employee Unique Code (e.g. SCS-1021)' })
  @IsString()
  @IsNotEmpty()
  employeeCode: string;

  @ApiProperty({ description: 'Full Legal Name' })
  @IsString()
  @IsNotEmpty()
  fullName: string;

  @ApiPropertyOptional({ description: 'Employee Photo (URL or Base64 String)' })
  @IsString()
  @IsOptional()
  photo?: string;

  @ApiProperty({ description: 'Job Designation' })
  @IsString()
  @IsNotEmpty()
  designation: string;

  @ApiProperty({ description: 'Company Department' })
  @IsString()
  @IsNotEmpty()
  department: string;

  @ApiProperty({ description: 'Assigned Branch Office' })
  @IsString()
  @IsNotEmpty()
  branch: string;

  @ApiPropertyOptional({ description: 'Name of Reporting Manager' })
  @IsString()
  @IsOptional()
  reportingManager?: string;

  @ApiProperty({ description: 'Contact Mobile Number' })
  @IsString()
  @IsNotEmpty()
  mobileNumber: string;

  @ApiProperty({ description: 'Corporate Email Address' })
  @IsEmail()
  @IsNotEmpty()
  email: string;

  @ApiProperty({ description: 'Aadhaar ID (Securely Masked)' })
  @IsString()
  @IsNotEmpty()
  aadhaarNumberMasked: string;

  @ApiProperty({ description: 'PAN Card Alpha-Numeric (Securely Masked)' })
  @IsString()
  @IsNotEmpty()
  panNumberMasked: string;

  @ApiProperty({ description: 'Date of Joining Service' })
  @IsDateString()
  @IsNotEmpty()
  dateOfJoining: string;

  @ApiProperty({ description: 'Date of Birth' })
  @IsDateString()
  @IsNotEmpty()
  dateOfBirth: string;

  @ApiPropertyOptional({ description: 'Blood Group' })
  @IsString()
  @IsOptional()
  bloodGroup?: string;

  @ApiProperty({ description: 'Residential Address text' })
  @IsString()
  @IsNotEmpty()
  address: string;

  @ApiProperty({ description: 'Emergency Contact Contact Number' })
  @IsString()
  @IsNotEmpty()
  emergencyContact: string;

  @ApiProperty({ description: 'Employment Type Status' })
  @IsString()
  @IsNotEmpty()
  employmentType: string; // FULL_TIME, CONTRACT, INTERN, PART_TIME

  @ApiProperty({ description: 'Bank Account Disbursal Number' })
  @IsString()
  @IsNotEmpty()
  bankAccountDetails: string;

  @ApiProperty({ description: 'IFSC Branch Code' })
  @IsString()
  @IsNotEmpty()
  ifscCode: string;

  @ApiPropertyOptional({ description: 'Universal Account Number (EPF)' })
  @IsString()
  @IsOptional()
  uan?: string;

  @ApiPropertyOptional({ description: 'ESIC Card ID' })
  @IsString()
  @IsOptional()
  esicNumber?: string;

  @ApiPropertyOptional({ description: 'Provident Fund Code' })
  @IsString()
  @IsOptional()
  pfNumber?: string;

  @ApiPropertyOptional({ description: 'Employee Active Lifecycle Status' })
  @IsString()
  @IsOptional()
  status?: string; // ACTIVE, INACTIVE
}

export class UpdateSalaryStructureDto {
  @ApiProperty({ description: 'Basic Salary Component' })
  @IsNumber()
  basicSalary: number;

  @ApiProperty({ description: 'House Rent Allowance' })
  @IsNumber()
  hra: number;

  @ApiProperty({ description: 'Special Corporate Allowances' })
  @IsNumber()
  specialAllowance: number;

  @ApiPropertyOptional({ description: 'Performance Incentives Pool' })
  @IsNumber()
  @IsOptional()
  incentive?: number;

  @ApiPropertyOptional({ description: 'Festival/Retention Bonus' })
  @IsNumber()
  @IsOptional()
  bonus?: number;

  @ApiPropertyOptional({ description: 'Overtime hours rate multiplier' })
  @IsNumber()
  @IsOptional()
  overtime?: number;

  @ApiPropertyOptional({ description: 'Business travel reimbursements' })
  @IsNumber()
  @IsOptional()
  reimbursements?: number;

  @ApiPropertyOptional({ description: 'PF Employee Contribution Deducted' })
  @IsNumber()
  @IsOptional()
  pf?: number;

  @ApiPropertyOptional({ description: 'ESIC Contribution Deducted' })
  @IsNumber()
  @IsOptional()
  esic?: number;

  @ApiPropertyOptional({ description: 'State Professional Tax Slab' })
  @IsNumber()
  @IsOptional()
  professionalTax?: number;

  @ApiPropertyOptional({ description: 'Income Tax TDS Monthly Deducted' })
  @IsNumber()
  @IsOptional()
  incomeTax?: number;
}

export class LogAttendanceDto {
  @ApiProperty({ description: 'Target Employee Unique ID' })
  @IsString()
  @IsNotEmpty()
  employeeId: string;

  @ApiProperty({ description: 'Calendar Date of Logs' })
  @IsDateString()
  @IsNotEmpty()
  date: string;

  @ApiProperty({ description: 'Status Indicator: PRESENT, ABSENT, HALF_DAY, LEAVE' })
  @IsString()
  @IsNotEmpty()
  status: string;

  @ApiPropertyOptional({ description: 'Time of Check In' })
  @IsDateString()
  @IsOptional()
  checkIn?: string;

  @ApiPropertyOptional({ description: 'Time of Check Out' })
  @IsDateString()
  @IsOptional()
  checkOut?: string;
}

export class ApplyLeaveDto {
  @ApiProperty({ description: 'Target Employee Unique ID' })
  @IsString()
  @IsNotEmpty()
  employeeId: string;

  @ApiProperty({ description: 'Leave Category: CASUAL, SICK, MATERNITY, PATERNITY, LOSS_OF_PAY' })
  @IsString()
  @IsNotEmpty()
  leaveType: string;

  @ApiProperty({ description: 'Leave Commences Date' })
  @IsDateString()
  @IsNotEmpty()
  startDate: string;

  @ApiProperty({ description: 'Leave Concludes Date' })
  @IsDateString()
  @IsNotEmpty()
  endDate: string;

  @ApiProperty({ description: 'Detailed justification text' })
  @IsString()
  @IsNotEmpty()
  reason: string;
}

export class ProcessPayrollDto {
  @ApiProperty({ description: 'Target Payroll Month (e.g. 2026-06)' })
  @IsString()
  @IsNotEmpty()
  month: string;

  @ApiPropertyOptional({ description: 'Filters to specific department' })
  @IsString()
  @IsOptional()
  department?: string;
}
