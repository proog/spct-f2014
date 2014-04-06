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
			string url = "http://localhost:9001";
			WebApp.Start<Startup>(url);
			PhoneHub.TagIdReceived += PhoneHubOnTagIdReceived;
			Console.WriteLine("Server running on {0}", url);
		}

		private void PhoneHubOnTagIdReceived(byte tagValue, string connectionId) {
			Console.WriteLine("Received identification message: " + tagValue);
			Phones.Add(tagValue, connectionId);
		}

		public void RequestImageDownloadToPhone(byte phoneTag, string url, string filename) {
			GlobalHost.ConnectionManager.GetHubContext<PhoneHub>().Clients.Client(Phones[phoneTag]).DownloadImageToPhone(url, filename);
		}

		public void RequestImageUploadToServer(byte phoneTag, string postUrl, string filename) {
			GlobalHost.ConnectionManager.GetHubContext<PhoneHub>().Clients.Client(Phones[phoneTag]).UploadImageToServer(postUrl, filename);
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

		public void SendTagId(byte tagValue) {
			TagIdReceived(tagValue, Context.ConnectionId);
		}
	}
}
