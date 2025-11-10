package com.dream11.odin.error;

import com.dream11.grpc.error.GrpcError;
import com.google.rpc.Code;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum OdinError implements GrpcError {
  SERVICE_UNKNOWN_EXCEPTION(
      "UNKNOWN_EXCEPTION",
      "Something went wrong. Loki is probably lurking in the system.",
      Code.UNKNOWN),
  ORGANIZATION_HEADER_NOT_FOUND("OAC_1", "orgId header cannot be null", Code.INVALID_ARGUMENT),
  ACCOUNT_DOES_NOT_EXIST(
      "OAC_2", "Provider account '%s' does not exist or no default account set", Code.NOT_FOUND),
  PROVIDER_SERVICE_ACCOUNT_DOES_NOT_EXIST(
      "OAC_3", "Provider service account '%d' does not exist", Code.NOT_FOUND),
  PROVIDER_ACCOUNT_NAME_NOT_FOUND("OAC_4", "Name is required", Code.INVALID_ARGUMENT),
  MAX_PROVIDER_ACCOUNT_NAME_COUNT(
      "OAC_5",
      "No of account names are more than the maximum allowed count: %s",
      Code.INVALID_ARGUMENT),
  PROVIDER_CATEGORY_NAME_NOT_FOUND(
      "OAC_6", "Provider category name is required", Code.INVALID_ARGUMENT),
  PROVIDER_CATEGORY_ALREADY_EXISTS(
      "OAC_7", "Provider category '%s' already exists", Code.ALREADY_EXISTS),
  PROVIDER_NAME_NOT_FOUND("OAC_8", "Provider name is required", Code.INVALID_ARGUMENT),
  PROVIDER_CATEGORY_ID_NOT_FOUND(
      "OAC_9", "Provider category ID is required", Code.INVALID_ARGUMENT),
  PROVIDER_DATA_SCHEMA_NOT_FOUND(
      "OAC_10", "Provider data schema is required", Code.INVALID_ARGUMENT),
  PROVIDER_ALREADY_EXISTS("OAC_11", "Provider '%s' already exists", Code.ALREADY_EXISTS),
  PROVIDER_CATEGORY_NOT_FOUND(
      "OAC_12", "Provider category with name '%s' does not exist", Code.NOT_FOUND),
  ACCOUNT_NAME_NOT_FOUND("OAC_13", "Account name is required", Code.INVALID_ARGUMENT),
  PROVIDER_DATA_NOT_FOUND("OAC_14", "Provider data is required", Code.INVALID_ARGUMENT),
  PROVIDER_ACCOUNT_CREATION_FAILED("OAC_15", "Provider account '%s' creation failed", Code.UNKNOWN),
  PROVIDER_ACCOUNT_MAPPING_CREATION_FAILED(
      "OAC_16", "Provider account mapping '%s' creation failed", Code.UNKNOWN),
  PROVIDER_NOT_FOUND("OAC_17", "Provider '%s' does not exist", Code.NOT_FOUND),
  PROVIDER_SERVICE_CATEGORY_NAME_NOT_FOUND(
      "OAC_18", "Provider service category name is required", Code.INVALID_ARGUMENT),
  PROVIDER_SERVICE_CATEGORY_ALREADY_EXISTS(
      "OAC_19", "Provider service category '%s' already exists", Code.ALREADY_EXISTS),
  PROVIDER_SERVICE_CATEGORY_CREATION_FAILED(
      "OAC_20", "Provider service category '%s' creation failed", Code.UNKNOWN),
  PROVIDER_SERVICE_NAME_NOT_FOUND(
      "OAC_21", "Provider service name is required", Code.INVALID_ARGUMENT),
  PROVIDER_SERVICE_DATA_SCHEMA_NOT_FOUND(
      "OAC_22", "Provider service data schema is required", Code.INVALID_ARGUMENT),
  PROVIDER_SERVICE_CATEGORY_NOT_FOUND(
      "OAC_23", "Provider service category '%s' does not exist", Code.NOT_FOUND),
  PROVIDER_SERVICE_ALREADY_EXISTS(
      "OAC_24",
      "Provider service already exists for provider '%s' and category '%s'",
      Code.ALREADY_EXISTS),
  PROVIDER_SERVICE_CREATION_FAILED("OAC_25", "Provider service '%s' creation failed", Code.UNKNOWN),
  PROVIDER_SERVICE_ACCOUNT_NAME_NOT_FOUND(
      "OAC_26", "Provider service account name is required", Code.INVALID_ARGUMENT),
  PROVIDER_SERVICE_ACCOUNT_DATA_NOT_FOUND(
      "OAC_27", "Provider service account data is required", Code.INVALID_ARGUMENT),
  PROVIDER_SERVICE_NOT_FOUND("OAC_28", "Provider service '%s' does not exist", Code.NOT_FOUND),
  PROVIDER_ACCOUNT_NOT_FOUND_BY_NAME(
      "OAC_29", "Provider account '%s' does not exist", Code.NOT_FOUND),
  PROVIDER_SERVICE_ACCOUNT_ALREADY_EXISTS(
      "OAC_30",
      "Provider service account already exists for service '%s' and account '%s'",
      Code.ALREADY_EXISTS),
  PROVIDER_SERVICE_ACCOUNT_CREATION_FAILED(
      "OAC_31", "Provider service account creation failed", Code.UNKNOWN),
  PROVIDER_ACCOUNT_ALREADY_EXISTS(
      "OAC_32", "Provider account with name %s already exists ", Code.INVALID_ARGUMENT);
  private final String errorCode;
  private final String errorMessage;
  private final Code grpcCode;
}
