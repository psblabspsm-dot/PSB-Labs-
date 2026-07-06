import { Controller, Get, Post, Body, Param, Query, Patch, HttpCode, HttpStatus } from '@nestjs/common';
import { ApiTags, ApiOperation, ApiResponse, ApiQuery } from '@nestjs/swagger';
import { HrmsService } from './hrms.service';
import { CreateEmployeeDto, UpdateSalaryStructureDto, LogAttendanceDto, ApplyLeaveDto, ProcessPayrollDto } from './dto/hrms.dto';

@ApiTags('Employee HRMS & Payroll Operations')
@Controller('api/v1/hrms')
export class HrmsController {
  constructor(private readonly hrmsService: HrmsService) {}

  @Get('employees')
  @ApiOperation({ summary: 'Retrieve full list of registered employees' })
  async getEmployees() {
    return this.hrmsService.getAllEmployees();
  }

  @Post('employees')
  @HttpCode(HttpStatus.CREATED)
  @ApiOperation({ summary: 'Register a new employee profile' })
  async createEmployee(@Body() dto: CreateEmployeeDto) {
    return this.hrmsService.createEmployee(dto);
  }

  @Get('employees/:id')
  @ApiOperation({ summary: 'Retrieve full profile details of a specific employee' })
  async getEmployeeById(@Param('id') id: string) {
    return this.hrmsService.getEmployeeById(id);
  }

  @Patch('employees/:id/status')
  @ApiOperation({ summary: 'Update employee active or inactive status' })
  async updateStatus(@Param('id') id: string, @Body('status') status: string) {
    return this.hrmsService.updateEmployeeStatus(id, status);
  }

  @Get('idcard/:employeeId')
  @ApiOperation({ summary: 'Retrieve generated digital ID card details' })
  async getIdCard(@Param('employeeId') employeeId: string) {
    return this.hrmsService.getIdCard(employeeId);
  }

  @Get('verify')
  @ApiOperation({ summary: 'Cryptographically verify an employee via QR link' })
  @ApiQuery({ name: 'id', description: 'Employee UUID' })
  @ApiQuery({ name: 'token', description: 'Secure signature hash' })
  async verifyEmployee(@Query('id') id: string, @Query('token') token: string) {
    return this.hrmsService.verifyEmployeeQr(id, token);
  }

  @Get('salary/:employeeId')
  @ApiOperation({ summary: 'Fetch salary structure details for an employee' })
  async getSalaryStructure(@Param('employeeId') employeeId: string) {
    return this.hrmsService.getSalaryStructure(employeeId);
  }

  @Patch('salary/:employeeId')
  @ApiOperation({ summary: 'Update or configure salary structure' })
  async updateSalaryStructure(@Param('employeeId') employeeId: string, @Body() dto: UpdateSalaryStructureDto) {
    return this.hrmsService.updateSalaryStructure(employeeId, dto);
  }

  @Post('attendance')
  @ApiOperation({ summary: 'Log check-in, check-out or attendance entry' })
  async logAttendance(@Body() dto: LogAttendanceDto) {
    return this.hrmsService.logAttendance(dto);
  }

  @Get('attendance/:employeeId')
  @ApiOperation({ summary: 'Fetch attendance logs for an employee' })
  async getAttendance(@Param('employeeId') employeeId: string) {
    return this.hrmsService.getAttendanceForEmployee(employeeId);
  }

  @Post('leaves')
  @ApiOperation({ summary: 'Submit a new leave application request' })
  async applyLeave(@Body() dto: ApplyLeaveDto) {
    return this.hrmsService.applyLeave(dto);
  }

  @Get('leaves')
  @ApiOperation({ summary: 'Fetch leave applications (optional employee filter)' })
  @ApiQuery({ name: 'employeeId', required: false })
  async getLeaves(@Query('employeeId') employeeId?: string) {
    return this.hrmsService.getLeaveRecords(employeeId);
  }

  @Patch('leaves/:id/status')
  @ApiOperation({ summary: 'Approve or reject a pending leave application' })
  async updateLeaveStatus(@Param('id') id: string, @Body('status') status: 'APPROVED' | 'REJECTED') {
    return this.hrmsService.updateLeaveStatus(id, status);
  }

  @Post('payroll/process')
  @ApiOperation({ summary: 'Process monthly payroll and auto-generate payslips' })
  async processPayroll(@Body() dto: ProcessPayrollDto) {
    return this.hrmsService.processPayroll(dto);
  }

  @Get('payslips')
  @ApiOperation({ summary: 'Retrieve list of generated payslips' })
  @ApiQuery({ name: 'employeeId', required: false })
  async getPayslips(@Query('employeeId') employeeId?: string) {
    return this.hrmsService.getPayslips(employeeId);
  }

  @Get('payslips/:id')
  @ApiOperation({ summary: 'Fetch full itemized breakdown of a specific payslip' })
  async getPayslipById(@Param('id') id: string) {
    return this.hrmsService.getPayslipById(id);
  }

  @Get('idcard/:employeeId/pdf')
  @ApiOperation({ summary: 'Generate a downloadable, high-fidelity tamper-proof PDF representation' })
  async downloadIdCardPdf(@Param('employeeId') employeeId: string) {
    return this.hrmsService.getSecureIdCardPdfHtml(employeeId);
  }
}
