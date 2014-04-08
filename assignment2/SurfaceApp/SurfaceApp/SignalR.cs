using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNet.SignalR;
using Microsoft.AspNet.SignalR.Hubs;
using Microsoft.Owin.Cors;
using Microsoft.Owin.Hosting;
using Owin;
using SurfaceApp.Network;

namespace SurfaceApp {
	class SignalR {
		private static SignalR singleton;
		public Dictionary<byte, string> Phones = new Dictionary<byte, string>();

		private SignalR() { }

		public static SignalR GetInstance() {
			if(singleton == null)
				singleton = new SignalR();
			return singleton;
		}

		public void Start() {
			string url = "http://*:9001";
			WebApp.Start<Startup>(url);
			PhoneHub.TagIdReceived += PhoneHubOnTagIdReceived;
			PhoneHub.DisconnectSignalReceived += PhoneHubOnDisconnectSignalReceived;
			Console.WriteLine("Server running on {0}", url);
		}

		public void Stop() {
			
		}

		private void PhoneHubOnTagIdReceived(byte tagValue, string connectionId) {
			Console.WriteLine("Received identification message: " + tagValue);
			Phones[tagValue] = connectionId;
			RequestAllImagesUploadToServer(tagValue, "/images/" + tagValue);
			//RequestImageDownloadToPhone(tagValue, "/images/20/app_chart.png", "app_chart.png");
		}

		private void PhoneHubOnDisconnectSignalReceived(byte tagValue) {
			Console.WriteLine("Received disconnect signal from " + tagValue);
			Phones.Remove(tagValue);
			ImageServer.GetInstance().RemoveDeviceUploadDir(tagValue);
		}

		public void RequestImageDownloadToPhone(byte phoneTag, string url, string filename) {
			GlobalHost.ConnectionManager.GetHubContext<PhoneHub>().Clients.Client(Phones[phoneTag]).DownloadImageToPhone(url, filename);
		}

		public void RequestImageUploadToServer(byte phoneTag, string postUrl, string filename) {
			GlobalHost.ConnectionManager.GetHubContext<PhoneHub>().Clients.Client(Phones[phoneTag]).UploadImageToServer(postUrl, filename);
		}

		public void RequestAllImagesUploadToServer(byte phoneTag, string postUrl) {
			GlobalHost.ConnectionManager.GetHubContext<PhoneHub>().Clients.Client(Phones[phoneTag]).UploadAllImagesToServer(postUrl);
		}
	}

	class Startup {
		public void Configuration(IAppBuilder app) {
			app.UseCors(CorsOptions.AllowAll);
			app.MapSignalR();
		}
	}

	public class PhoneHub : Hub {
		public static event Action<byte, string> TagIdReceived;
		public static event Action<byte> DisconnectSignalReceived;

		public void SendTagId(byte tagValue) {
			if(TagIdReceived != null)
				TagIdReceived(tagValue, Context.ConnectionId);
		}

		public void SendDisconnectSignal(byte tagValue) {
			if(DisconnectSignalReceived != null)
				DisconnectSignalReceived(tagValue);
		}
	}
}
