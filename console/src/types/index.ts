export interface Metadata {
  name: string;
  labels?: {
    [key: string]: string;
  } | null;
  annotations?: {
    [key: string]: string;
  } | null;
  version?: number | null;
  creationTimestamp?: string | null;
  deletionTimestamp?: string | null;
}


export interface TemplateOption {
  name: string;
  label: string,
  value: string,
  desc: string;
}


export interface Option {
  label: string,
  value: string,
}

export interface EmailTemplateExtension {
  apiVersion: string;
  kind: string;
  metadata: Metadata;
  spec: Spec;
}

export interface Spec {
  template: string;
  enable: boolean;
}
