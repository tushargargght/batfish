package org.batfish.coordinator;

// Include the following imports to use queue APIs.
import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.batfish.common.BatfishLogger;
import org.batfish.coordinator.authorizer.*;
import org.batfish.coordinator.config.ConfigurationLocator;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jettison.JettisonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class Main {

   private static Authorizer _authorizer;
   private static BatfishLogger _logger;
   private static PoolMgr _poolManager;
   private static Settings _settings;
   private static WorkMgr _workManager;

   public static Authorizer getAuthorizer() {
      return _authorizer;
   }

   public static BatfishLogger getLogger() {
      return _logger;
   }

   public static PoolMgr getPoolMgr() {
      return _poolManager;
   }

   public static Settings getSettings() {
      return _settings;
   }

   public static WorkMgr getWorkMgr() {
      return _workManager;
   }

   private static void initAuthorizer() {
      switch (_settings.getAuthorizationType()) {
      case none:
         _authorizer = new NoneAuthorizer();
         break;
      case file:
         _authorizer = new FileAuthorizer();
         break;
      case database:
      default:
         System.err
               .print("org.batfish.coordinator: Initialization failed. Unsupported authorizer type "
                     + _settings.getAuthorizationType());
         System.exit(1);
      }
   }

   private static void initPoolManager() {

      ResourceConfig rcPool = new ResourceConfig(PoolMgrService.class)
            .register(new JettisonFeature()).register(MultiPartFeature.class)
            .register(org.batfish.coordinator.CrossDomainFilter.class);

      if (!_settings.getUseSsl()) {
         URI poolMgrUri = UriBuilder
               .fromUri("http://" + _settings.getServiceHost())
               .port(_settings.getServicePoolPort()).build();

         _logger.info("Starting pool manager at " + poolMgrUri + "\n");

         GrizzlyHttpServerFactory.createHttpServer(poolMgrUri, rcPool);
      }
      else {
         URI poolMgrUri = UriBuilder
               .fromUri("https://" + _settings.getServiceHost())
               .port(_settings.getServicePoolPort()).build();

         _logger.info("Starting pool manager at " + poolMgrUri + "\n");

         File keystoreFile = Paths.get(
               org.batfish.common.Util.getJarOrClassDir(
                     ConfigurationLocator.class).getAbsolutePath(),
               _settings.getSslKeystoreFilename()).toFile();

         if (!keystoreFile.exists()) {
            System.err
                  .print("org.batfish.coordinator: keystore file not found: "
                        + keystoreFile.getAbsolutePath());
            System.exit(1);
         }

         SSLContextConfigurator sslCon = new SSLContextConfigurator();
         sslCon.setKeyStoreFile(keystoreFile.getAbsolutePath());
         sslCon.setKeyStorePass(_settings.getSslKeystorePassword());

         GrizzlyHttpServerFactory.createHttpServer(poolMgrUri, rcPool, true,
               new SSLEngineConfigurator(sslCon, false, false, false));
      }

      _poolManager = new PoolMgr(_logger);

   }

   private static void initWorkManager() {
      ResourceConfig rcWork = new ResourceConfig(WorkMgrService.class)
            .register(new JettisonFeature()).register(MultiPartFeature.class)
            .register(org.batfish.coordinator.CrossDomainFilter.class);

      if (!_settings.getUseSsl()) {
         URI workMgrUri = UriBuilder
               .fromUri("http://" + _settings.getServiceHost())
               .port(_settings.getServiceWorkPort()).build();

         _logger.info("Starting work manager at " + workMgrUri + "\n");

         GrizzlyHttpServerFactory.createHttpServer(workMgrUri, rcWork);
      }
      else {
         URI workMgrUri = UriBuilder
               .fromUri("https://" + _settings.getServiceHost())
               .port(_settings.getServiceWorkPort()).build();

         _logger.info("Starting work manager at " + workMgrUri + "\n");

         File keystoreFile = Paths.get(
               org.batfish.common.Util.getJarOrClassDir(
                     ConfigurationLocator.class).getAbsolutePath(),
               _settings.getSslKeystoreFilename()).toFile();

         if (!keystoreFile.exists()) {
            System.err
                  .print("org.batfish.coordinator: keystore file not found: "
                        + keystoreFile.getAbsolutePath());
            System.exit(1);
         }

         SSLContextConfigurator sslCon = new SSLContextConfigurator();
         sslCon.setKeyStoreFile(keystoreFile.getAbsolutePath());
         sslCon.setKeyStorePass(_settings.getSslKeystorePassword());

         GrizzlyHttpServerFactory.createHttpServer(workMgrUri, rcWork, true,
               new SSLEngineConfigurator(sslCon, false, false, false));
      }

      _workManager = new WorkMgr(_logger);
   }

   public static void main(String[] args) {
      _settings = null;
      try {
         _settings = new Settings(args);
         _logger = new BatfishLogger(_settings.getLogLevel(), false,
               _settings.getLogFile(), false);
      }
      catch (Exception e) {
         System.err.print("org.batfish.coordinator: Initialization failed: "
               + e.getMessage());
         System.exit(1);
      }

      initAuthorizer();
      initPoolManager();
      initWorkManager();

      // sleep indefinitely, in 10 minute chunks
      try {
         while (true) {
            Thread.sleep(10 * 60 * 1000); // 10 minutes
            _logger.info("Still alive .... waiting for work to show up\n");
         }
      }
      catch (Exception ex) {
         String stackTrace = ExceptionUtils.getFullStackTrace(ex);
         System.err.println(stackTrace);
      }
   }
}