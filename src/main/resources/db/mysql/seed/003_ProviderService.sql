--liquibase formatted sql

--changeset odin:003_provider_service
INSERT INTO `provider_service`
  VALUES (1, NOW(), NOW(), 1, 'EKS', '{
    "type": "object",
    "required": [
      "clusters",
    ],
    "properties": {
      "clusters": {
        "type": "array",
        "items": {
          "type": "object",
          "required": [
              "name",
              "kubeconfig"
          ],
          "properties": {
            "name": {
              "type": "string"
            },
            "kubeconfig": {
              "type": "string"
            },
            "namespaceConfig": {
              "type": "object",
              "required": [
                "provider"
              ],
              "properties": {
                "provider": {
                  "type": "object",
                  "required": [
                      "name",
                      "config"
                  ],
                  "properties": {
                    "name": {
                      "type": "string"
                    },
                    "config": {
                      "type": "object"
                    }
                  }
                }
              }
            },
            "labels": {
              "type": "object"
            }
          }
        }
      },
      "pullSecrets": {
          "type": "array"
      },
      "serviceAnnotations": {
          "type": "object"
      },
      "environmentVariables": {
          "type": "object"
      },
      "serviceAccountAnnotations": {
          "type": "object"
      }
    }
  }', 1, 1),
         (2, NOW(), NOW(), 1, 'VPC', '{
           "type": "object",
           "required": [
             "vpcId",
             "ec2Subnets",
             "ec2SecurityGroups",
             "lbSubnets",
             "lbSecurityGroups"
           ],
           "properties": {
             "vpcId": {
               "type": "string"
             },
             "lbSubnets": {
               "type": "object",
               "required": [
                 "private",
                 "public"
               ],
               "properties": {
                 "public": {
                   "type": "array",
                   "items": {
                     "type": "string"
                   }
                 },
                 "private": {
                   "type": "array",
                   "items": {
                     "type": "string"
                   }
                 }
               }
             },
             "ec2Subnets": {
               "type": "object",
               "required": [
                 "public",
                 "private"
               ],
               "properties": {
                 "public": {
                   "type": "array",
                   "items": {
                     "type": "string"
                   }
                 },
                 "private": {
                   "type": "array",
                   "items": {
                     "type": "string"
                   }
                 }
               }
             },
             "lbSecurityGroups": {
               "type": "object",
               "required": [
                 "external",
                 "internal"
               ],
               "properties": {
                 "external": {
                   "type": "array",
                   "items": {
                     "type": "string"
                   }
                 },
                 "internal": {
                   "type": "array",
                   "items": {
                     "type": "string"
                   }
                 }
               }
             },
             "ec2SecurityGroups": {
               "type": "object",
               "required": [
                 "internal",
                 "external"
               ],
               "properties": {
                 "external": {
                   "type": "array",
                   "items": {
                     "type": "string"
                   }
                 },
                 "internal": {
                   "type": "array",
                   "items": {
                     "type": "string"
                   }
                 }
               }
             }
           }
         }', 1, 3),
         (3, NOW(), NOW(), 1, 'EC2', '{
           "type": "object",
           "required": [
             "iamInstanceProfile",
             "ec2KeyName",
             "ami",
             "userData",
             "tags"
           ],
           "properties": {
             "ami": {
               "type": "object",
               "properties": {
                 "sharedAccountIds": {
                   "type": "array",
                   "items": {}
                 }
               }
             },
             "tags": {
               "type": "object",
               "required": [],
               "properties": {}
             },
             "userData": {
               "type": "object",
               "properties": {
                 "preStart": {
                   "type": "string"
                 },
                 "postStart": {
                   "type": "string"
                 },
                 "environmentVariables": {
                   "type": "object",
                   "required": [],
                   "properties": {}
                 }
               }
             },
             "ec2KeyName": {
               "type": "string"
             },
             "iamInstanceProfile": {
               "type": "string"
             }
           }
         }', 1, 2),
         (4, NOW(), NOW(), 1, 'ACM', '{
           "type": "object",
           "required": [
             "sslCertArn"
           ],
           "properties": {
             "sslCertArn": {
               "type": "string"
             }
           }
         }', 1, 5),
         (5, NOW(), NOW(), 1, 'R53', '{
           "type": "object",
           "required": [
             "domains",
             "assumeRoleARN",
             "region"
           ],
           "properties": {
             "region": {
               "type": "string"
             },
             "domains": {
               "type": "array",
               "items": {
                 "type": "object",
                 "required": [
                   "name",
                   "id"
                 ],
                 "properties": {
                   "id": {
                     "type": "string"
                   },
                   "name": {
                     "type": "string"
                   }
                 }
               }
             },
             "assumeRoleARN": {
               "type": "string"
             }
           }
         }', 1, 4),
         (6, NOW(), NOW(), 1, 'RDS', '{
           "type": "object",
           "required": [
             "subnetGroup"
           ],
           "properties": {
             "iamRoles": {
               "type": "array",
               "items": {
                 "type": "string"
               }
             },
             "subnetGroup": {
               "type": "string"
             }
           }
         }', 1, 6),
         (7, NOW(), NOW(), 1, 'Elasticache', '{
           "type": "object",
           "required": [
             "subnetGroup",
             "securityGroups"
           ],
           "properties": {
             "subnetGroup": {
               "type": "string"
             },
             "securityGroups": {
               "type": "array",
               "items": {
                 "type": "string"
               }
             }
           }
         }', 1, 7),
         (8, NOW(), NOW(), 1, 'DockerRegistry', '{
           "type": "object",
           "required": [
             "server",
             "registry",
             "username",
             "password"
           ],
           "properties": {
             "server": {
               "type": "string"
             },
             "registry": {
               "type": "string"
             },
             "username": {
               "type": "string"
             },
             "password": {
               "type": "string"
             }
             "insecure": {
               "type": "boolean"
             },
             "allowPush": {
               "type": "boolean "
             }
           }
         }', 2, 9),
         (9, NOW(), NOW(), 1, 'Storage', '{
           "type": "object",
           "required": [
             "artifacts"
           ],
           "properties": {
             "artifacts": {
               "type": "object",
               "required": [
                 "repository"
               ],
               "properties": {
                 "token": {
                   "type": "string"
                 },
                 "purpose": {
                   "type": "string"
                 },
                 "repository": {
                   "type": "string"
                 }
               }
             }
           }
         }', 2, 10),
         (10, NOW(), NOW(), 1, 'HelmRegistry', '{
           "type": "object",
           "required": [
             "url",
             "username",
             "password"
           ],
           "properties": {
             "url": {
               "type": "string"
             },
             "password": {
               "type": "string"
             },
             "username": {
               "type": "string"
             }
           }
         }', 2, 12),
         (11, NOW(), NOW(), 1, 'DiscoveryConfig', '{
           "type": "object",
           "required": [
             "discoveryAnnotations"
           ],
           "properties": {
             "discoveryAnnotations": {
               "type": "string"
             }
           }
         }', 3, 4)

    AS NEW
ON DUPLICATE KEY UPDATE id                           = NEW.id,
                        version                      = NEW.version,
                        name                         = NEW.name,
                        provider_service_category_id = NEW.provider_service_category_id,
                        data_schema                  = NEW.data_schema,
                        provider_id                  = NEW.provider_id;
