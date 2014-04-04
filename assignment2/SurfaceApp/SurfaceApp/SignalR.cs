using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNet.SignalR;
using Microsoft.Owin.Cors;
using Microsoft.Owin.Hosting;
using Owin;

namespace SurfaceApp {
	class SignalR {
		private Dictionary<byte, dynamic> phones = new Dictionary<byte, dynamic>();

		public void Start() {
			string url = "http://localhost:8080";
			using (WebApp.Start(url)) {
				Console.WriteLine("Server running on {0}", url);
				//Console.ReadLine();
			}
		}
	}

	class Startup {
		public void Configuration(IAppBuilder app) {
			app.UseCors(CorsOptions.AllowAll);
			app.MapSignalR();
		}
	}

	public class PhoneHub : Hub {
		public void SendIdentification(byte tagValue) {
			Console.WriteLine("Received identification message: " + tagValue);
		}
	}
}
