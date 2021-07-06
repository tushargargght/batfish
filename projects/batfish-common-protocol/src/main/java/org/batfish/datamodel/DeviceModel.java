package org.batfish.datamodel;

/**
 * A specific vendor and hardware (maybe virtual) type. Examples include Juniper SRX340, AWS EC2
 * Instance.
 *
 * @see DeviceType for the generic classification of a device.
 */
public enum DeviceModel {
  ARISTA_UNSPECIFIED,
  AWS_EC2_INSTANCE,
  AWS_ELASTICSEARCH_DOMAIN,
  AWS_ELB_NETWORK,
  AWS_INTERNET_GATEWAY,
  AWS_NAT_GATEWAY,
  AWS_RDS_INSTANCE,
  AWS_SERVICES_GATEWAY,
  AWS_SUBNET_PRIVATE,
  AWS_SUBNET_PUBLIC,
  AWS_TRANSIT_GATEWAY,
  AWS_VPC,
  AWS_VPC_ENDPOINT_GATEWAY,
  AWS_VPC_ENDPOINT_INTERFACE,
  AWS_VPN_GATEWAY,
  BATFISH_INTERNET,
  BATFISH_ISP,
  CISCO_UNSPECIFIED,
  CHECK_POINT_GATEWAY,
  CUMULUS_UNSPECIFIED,
  F5_UNSPECIFIED,
  FORTIOS_UNSPECIFIED,
  JUNIPER_UNSPECIFIED,
  PALO_ALTO_FIREWALL,
  PALO_ALTO_PANORAMA,
}
