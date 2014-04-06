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
		public Dictionary<byte, dynamic> phones = new Dictionary<byte, dynamic>();

		private SignalR() { }

		public static SignalR GetInstance() {
			if(singleton == null)
				singleton = new SignalR();
			return singleton;
		}

		public void Start() {
			string url = "http://localhost:9001";
			WebApp.Start<Startup>(url);
			Console.WriteLine("Server running on {0}", url);
		}
	}

	class Startup {
		public void Configuration(IAppBuilder app) {
			app.UseCors(CorsOptions.AllowAll);
			app.MapSignalR();
		}
	}

	public class PhoneHub : Hub {
		public void SendTagId(byte tagValue) {
			Console.WriteLine("Received identification message: " + tagValue);
			SignalR.GetInstance().phones.Add(tagValue, Clients.Caller);
		}
	}
}
